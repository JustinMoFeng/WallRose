package com.shingekinokyojin.wallrose.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.AlarmClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.base.Unit
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.bean.geo.GeoBean.LocationBean
import com.qweather.sdk.view.QWeather
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.data.ChatsRepository
import com.shingekinokyojin.wallrose.model.AlarmArgument
import com.shingekinokyojin.wallrose.model.ToolCall
import com.shingekinokyojin.wallrose.model.WeatherArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.Period
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class ChatViewModel(
    private val chatsRepository: ChatsRepository,
    private val context: Context
) : ViewModel() {

    // LiveData for chat messages
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> = _messages
    var responding by mutableStateOf(false)
    var currentMessage by mutableStateOf("")
    var inputMessage by mutableStateOf("")
    var chatStatus by mutableStateOf("")

    var loginStatus by mutableStateOf(true)

    var currentChatId by mutableStateOf("")
    var haveLocation by mutableStateOf(false)
    var gettingLocation by mutableStateOf(false)

    var showWordPart by mutableStateOf(false)


    init {
        viewModelScope.launch{
            haveLocation = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun sendMessage(message:String){

        if (responding) {
            return
        }
        var toolFlag = false
        responding = true
        inputMessage = ""
        currentMessage = "..."
        Log.d("ChatViewModel", "Sending $inputMessage")
        viewModelScope.launch(Dispatchers.IO) {
            if(currentChatId == ""){
                currentChatId = chatsRepository.createChat().toString()
                if(currentChatId == ""){
                    loginStatus = false
                    currentMessage = ""
                    responding = false
                    return@launch
                }
                Log.d("ChatViewModel", "Created chat $currentChatId")
            }
            chatsRepository.sendMessage(message,currentChatId).collect { chatEvent ->
                when(chatEvent.event){
                    "error" -> {
                        Log.e("ChatViewModel", "Received $chatEvent")
                        chatStatus = "error"
                        currentMessage = chatEvent.data
                        loginStatus = false
                        return@collect
                    }
                    "message" -> {
                        if (currentMessage == "...") {
                            currentMessage = ""
                        }
                        Log.d("ChatViewModel", "Received $chatEvent")
                        currentMessage += chatEvent.data
                    }
                    "tool_call" -> {
                        val toolObj = Json.decodeFromString<ToolCall>(chatEvent.data)
                        toolFlag = true
                        val returnStr = when(toolObj.function.name){
                            "set_alarm" -> setAlarmClock(toolObj.function.arguments)
                            "get_weather" -> getCurrentWeather(toolObj.function.arguments)

                            else -> "unknown function"
                        }

                        chatsRepository.returnMessage(returnStr,currentChatId,toolObj.id,toolObj.function.name)

                        Log.d("ChatViewModel", "Received $chatEvent")

                    }
                    "chat_id" -> {
                        currentChatId = chatEvent.data
                    }
                }
            }

            responding = false

            if(toolFlag){
                sendMessage("")
            }
        }
    }

    private fun setAlarmClock(arguments: String):String {
        try {
            val alarmArg = Json.decodeFromString<AlarmArgument>(arguments)
            Log.d("ChatViewModel", "Received $alarmArg")

            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            val time = alarmArg.time
            val hour = time.split(":")[0].toInt()
            val minute = time.split(":")[1].toInt()
            intent.apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, alarmArg.name)
                putExtra(AlarmClock.EXTRA_VIBRATE, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return "success"
        }catch (e: Exception){
            Log.e("ChatViewModel", "Error: $e")
            return "fail"
        }
    }

    private suspend fun getCurrentWeather(arguments: String): String{
        try {
            if (!haveLocation) {
                gettingLocation = true
                return "失败，没有获得地理位置权限"
            }

            val weatherArg = Json.decodeFromString<WeatherArgument>(arguments)
            Log.d("ChatViewModel", "Received $weatherArg")
            val city = if (weatherArg.location == "current") {
                val currentLocation = getCurrentLocation() ?: return "失败，没有获得地理位置"
                currentLocation
            } else findCityId(weatherArg.location)
            val weatherArgDate = LocalDate.parse(weatherArg.date)
            // 东八区时间
            val currentDate = LocalDate.now()
            val period = Period.between(currentDate, weatherArgDate)
            val daysDifference = period.days
            Log.d("ChatViewModel", daysDifference.toString())

            if (daysDifference == 0) {
                return suspendCancellableCoroutine<String> { continuation ->
                    QWeather.getWeatherNow(
                        context,
                        city.id,
                        Lang.ZH_HANS,
                        Unit.METRIC,
                        object : QWeather.OnResultWeatherNowListener {
                            override fun onError(e: Throwable?) {
                                Log.e("ChatViewModel", "Error: $e")
                                continuation.resumeWithException(e ?: Exception("Unknown error"))
                            }

                            override fun onSuccess(p0: com.qweather.sdk.bean.weather.WeatherNowBean?) {
                                Log.d("ChatViewModel", "Success: $p0")
                                val weatherText = p0?.now?.text.toString()
                                val temperature = p0?.now?.temp.toString()
                                val precipitation = p0?.now?.precip.toString()
                                val humidity = p0?.now?.humidity.toString()
                                continuation.resume("${city.name} 天气: $weatherText, $temperature℃, 降水量: $precipitation mm, 湿度: $humidity%")
                            }
                        }
                    )
                }
            } else {
                return "Weather information is not available for future dates"
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error: $e")
            return "fail"
        }
    }

    suspend fun getCurrentLocation(): LocationBean? {
        gettingLocation = true
        Log.d("ChatViewModel", "Getting current location")
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("ChatViewModel", "No location permission")
            haveLocation = false
            return null
        }

        val latestLocation = locationManager.getLastKnownLocation(provider)
        if (latestLocation == null) {
            Log.d("ChatViewModel", "No last known location")
            return null
        }

        // 以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）
        val locationString = "${latestLocation.longitude},${latestLocation.latitude}"
        Log.d("ChatViewModel", "Location: $locationString")

        return suspendCancellableCoroutine { continuation ->
            QWeather.getGeoCityLookup(
                context,
                locationString,
                Lang.ZH_HANS,
                object : QWeather.OnResultGeoListener {
                    override fun onError(e: Throwable?) {
                        Log.e("ChatViewModel", "Error: $e")
                        continuation.resumeWithException(e ?: Exception("Unknown error"))
                    }

                    override fun onSuccess(geoBean: GeoBean?) {
                        Log.d("ChatViewModel", "Success: $geoBean")
                        val resCity = geoBean?.locationBean?.get(0) ?: LocationBean()
                        continuation.resume(resCity)
                    }
                }
            )
        }
    }

    private suspend fun findCityId(cityName: String): LocationBean {

        return suspendCancellableCoroutine { continuation ->
            QWeather.getGeoCityLookup(
                context,
                cityName,
                Lang.ZH_HANS,
                object : QWeather.OnResultGeoListener {
                    override fun onError(e: Throwable?) {
                        Log.e("ChatViewModel", "Error: $e")
                        continuation.resumeWithException(e ?: Exception("Unknown error"))
                    }

                    override fun onSuccess(geoBean: GeoBean?) {
                        Log.d("ChatViewModel", "Success: $geoBean")
                        val resCity = geoBean?.locationBean?.get(0) ?: LocationBean()
                        continuation.resume(resCity)
                    }
                }
            )
        }
    }



    fun clearMessages() {
        _messages.value = emptyList()
    }

    fun getGreeting() {
        if (responding) {
            return
        }
        responding = true
        inputMessage = ""
        currentMessage = "..."
        Log.d("ChatViewModel", "Sending $inputMessage")
        viewModelScope.launch(Dispatchers.IO) {
            chatsRepository.getGreeting(inputMessage).collect { chatEvent ->
                if(chatEvent.event == "error") {
                    chatStatus = "error"
                    currentMessage = chatEvent.data
                    return@collect
                }else if (currentMessage == "...") {
                    currentMessage = ""
                }
                Log.d("ChatViewModel", "Received $chatEvent")
                currentMessage += chatEvent.data
            }
            responding = false
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WallRoseApplication)
                val chatsRepository = application.container.chatsRepository
                ChatViewModel(chatsRepository,application.applicationContext)
            }
        }
    }
}
