package com.shingekinokyojin.wallrose.ui.composables.chat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.opengl.GLSurfaceView
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.MainActivity
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.live2d.GLRendererMinimum
import com.shingekinokyojin.wallrose.live2d.LAppMinimumDelegate
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseAppBar
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDrawer
import com.shingekinokyojin.wallrose.ui.screens.ChatViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    navController: NavController
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            WallRoseAppBar(
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
        val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
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
                sendAction = chatViewModel::getMessages
            )
        }

    }

}

@Composable
fun ChatBody(
    modifier: Modifier = Modifier,
    currentMessage: String,
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
                GLSurfaceView(it).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(GLRendererMinimum())
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            },
            update = { glSurfaceView ->
                // This is a simplified example. Depending on your app's requirements,
                // you might need a more robust way to handle lifecycle events.
                // 获取Activity
                // Observing lifecycle changes
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

            }
        )

        ChatFeedBack(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.BottomCenter)
                .height(180.dp)
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
        .background(color = MaterialTheme.colorScheme.background, RoundedCornerShape(15.dp))
        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.TopCenter,

    ){
        Text(
            text = word,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopCenter)
                .padding(top = 10.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary
            ),
        )
    }

}


/**
 * 聊天底部输入框
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatBottomInputPart(
    modifier: Modifier = Modifier,
    sendAction: () -> Unit
){

    var text by remember { mutableStateOf("") }
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
                    text = it
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
