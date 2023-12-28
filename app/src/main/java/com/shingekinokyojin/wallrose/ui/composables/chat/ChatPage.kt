package com.shingekinokyojin.wallrose.ui.composables.chat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.shingekinokyojin.wallrose.MainActivity
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.live2d.GLRendererMinimum
import com.shingekinokyojin.wallrose.live2d.LAppMinimumDelegate
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDrawer
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseTabAppBar
import com.shingekinokyojin.wallrose.ui.screens.ChatViewModel
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel,
    navController: NavController,
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    if(chatViewModel.currentMessage=="")chatViewModel.getGreeting()

    Scaffold(
        modifier = modifier,
        topBar = {
            WallRoseTabAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                title = stringResource(id = R.string.tabbar_chat),
                onLeftClick = {
                    if(drawerState.isOpen) scope.launch { drawerState.close() }
                    else scope.launch { drawerState.open() }
                }
            )
        },

    ) {
        ModalNavigationDrawer(
            drawerContent = {
                WallRoseDrawer(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth(0.648f)
                        .fillMaxHeight()
                        .padding(it)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            gesturesEnabled = false,
            drawerState = drawerState,
        ) {
            ChatBody(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                currentMessage = chatViewModel.currentMessage,
                inputMessage = chatViewModel.inputMessage,
                changeText = { chatViewModel.inputMessage = it },
                sendAction = {
                    if(SharedPreferencesManager.getToken()==""){
                        navController.navigate(RouteConfig.ROUTE_LOGIN)
                    }else{
                        Log.d("ChatPage", "sendAction")
                        chatViewModel.sendMessage(chatViewModel.inputMessage)
                    }
                }
            )
        }

        if(chatViewModel.chatStatus == "error") {
            AlertDialog(
                onDismissRequest = {
                    chatViewModel.chatStatus = ""
                },
                title = {
                    Text(text = "Error")
                },
                text = {
                    Text(text = chatViewModel.currentMessage)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            chatViewModel.chatStatus = ""
                            navController.navigate(RouteConfig.ROUTE_LOGIN)
                        }
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }

    }

}

@Composable
fun ChatBody(
    modifier: Modifier = Modifier,
    currentMessage: String,
    inputMessage: String,
    changeText: (String) -> Unit = {},
    sendAction: () -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChatLive2d(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            currentMessage = currentMessage
        )

        ChatBottomInputPart(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .weight(0.1f),
            text = inputMessage,
            changeText = changeText,
            sendAction = sendAction
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatLive2d(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    currentMessage: String
){
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    val pointX = event.x
                    val pointY = event.y

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> LAppMinimumDelegate
                            .getInstance()
                            .onTouchBegan(pointX, pointY)

                        MotionEvent.ACTION_UP -> LAppMinimumDelegate
                            .getInstance()
                            .onTouchEnd(pointX, pointY)

                        MotionEvent.ACTION_MOVE -> LAppMinimumDelegate
                            .getInstance()
                            .onTouchMoved(pointX, pointY)
                    }
                    true
                },
            factory = {
                Log.d("ChatLive2d", "factory")
                val glSurfaceView = GLSurfaceView(it).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(GLRendererMinimum())
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }

                val lifecycleObserver = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> LAppMinimumDelegate.getInstance().onStart(MainActivity.instance)
                        Lifecycle.Event.ON_RESUME -> glSurfaceView.onResume()
                        Lifecycle.Event.ON_PAUSE -> {
                            glSurfaceView.onPause()
                            LAppMinimumDelegate.getInstance().onPause()
                        }

                        Lifecycle.Event.ON_STOP -> LAppMinimumDelegate.getInstance().onStop()
                        Lifecycle.Event.ON_DESTROY -> LAppMinimumDelegate.getInstance().onDestroy()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

                glSurfaceView
            },
            update = { _ ->
                // This is a simplified example. Depending on your app's requirements,
                // you might need a more robust way to handle lifecycle events.
                // 获取Activity
                // Observing lifecycle changes
                Log.d("ChatLive2d", "update")
            }
        )

        ChatFeedBack(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            word = currentMessage
        )
    }
}

@Composable
fun ChatFeedBack(
    modifier: Modifier = Modifier,
    word:String
){
    Box(modifier = modifier
        .fillMaxWidth()
        .requiredHeightIn(min = 120.dp, max = 220.dp)
        .background(color = MaterialTheme.colorScheme.background, RoundedCornerShape(15.dp))
        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.TopCenter,

    ){
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = word,
                modifier = modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary
                ),
            )
        }
    }

}

@Composable
fun ChatTextBody(
    modifier: Modifier = Modifier
){
    WallRoseTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 10.dp, end = 10.dp)
        ) {

        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ChatMyMessage(
    modifier: Modifier,
    myAvatarUrl: String,
    myMessage: String,
    myUserName: String
){
    WallRoseTheme{

        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = rememberImagePainter(myAvatarUrl),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 5.dp, top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(start = 10.dp, end = 10.dp),
            ) {

                Text(
                    text = myUserName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 5.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = myMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                )
            }
        }
    }

}


/**
 * 聊天底部输入框
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatBottomInputPart(
    modifier: Modifier = Modifier,
    text: String,
    changeText: (String) -> Unit = {},
    sendAction: () -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current

    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.chat_voiceinput),
                contentDescription = "语音输入",
                modifier = Modifier
                    .weight(1f)
                    .size(33.dp)
                    .clip(CircleShape)

            )

            BasicTextField(
                value = text,
                onValueChange = {
                    changeText(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                    .weight(5f)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(5.dp)),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp)
                            .padding(vertical = 5.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        innerTextField()
                    }
                },
                singleLine = false
            )

            Image(
                painter = painterResource(id = R.drawable.chat_emoji),
                contentDescription = "表情",
                modifier = Modifier
                    .weight(1f)
                    .size(33.dp)
            )

            Button(
                onClick = {

                    sendAction()
                },
                modifier = Modifier
                    .weight(1.8f)
                    .height(40.dp)
                    .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50.dp))
                    .padding(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7F7F)
                )
            )
            {
                Text(
                    text = "发送",
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}





@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ChatBottomInputPartPreview(){
    ChatBottomInputPart(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        text = "123",
        sendAction = {}
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ChatLive2dPreview(){
    ChatLive2d(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        currentMessage = "123"
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ChatMyMessagePreview(){
    ChatMyMessage(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        myAvatarUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/ChatGPT_logo.svg/1200px-ChatGPT_logo.svg.png",
        myMessage = "在 Android Studio 中，使用 Jetpack Compose 的 @Preview 注解时，您可能会发现无法预览从网络加载的图片。这是因为 @Preview 功能有一些限制，主要包括：\n" +
                "\n" +
                "网络访问限制：@Preview 注解仅用于在 Android Studio 中以静态方式展示 UI 组件的布局和样式。它不支持网络访问，因此无法直接加载网络图片。\n" +
                "\n" +
                "执行环境：@Preview 在 Android Studio 的设计视图中运行，而非真实的设备或模拟器环境。因此，它不执行完整的运行时操作，如网络请求。\n" +
                "\n" +
                "安全性和性能：限制网络访问可以保护您的计算机免受潜在的不安全内容的影响，并且可以提高预览的生成速度。",
        myUserName = "ChatGPT"
    )
}
