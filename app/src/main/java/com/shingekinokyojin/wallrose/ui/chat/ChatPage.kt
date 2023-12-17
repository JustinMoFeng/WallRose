package com.shingekinokyojin.wallrose.ui.chat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.opengl.GLSurfaceView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme


@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    navController: NavController
){
    Text(text = "chatPage")
    Button(onClick = { navController.navigate(RouteConfig.ROUTE_PROFILE) }) {
        Text(text = "goProfile")
    }
}




/**
 * 聊天底部输入框
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatBottomInputPart(
    modifier: Modifier = Modifier
){

    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp, start = 5.dp, end = 5.dp, top = 5.dp)
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.Bottom
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
                onClick = { /*TODO*/  },
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

@Composable
fun ChatBodyPart(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        AndroidView(
            factory = {
                GLSurfaceView(it).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(object : GLSurfaceView.Renderer {
                        override fun onSurfaceCreated(
                            gl: javax.microedition.khronos.opengles.GL10?,
                            config: javax.microedition.khronos.egl.EGLConfig?
                        ) {
                            // TODO
                        }

                        override fun onSurfaceChanged(
                            gl: javax.microedition.khronos.opengles.GL10?,
                            width: Int,
                            height: Int
                        ) {
                            // TODO
                        }

                        override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
                            // TODO
                        }
                    })
                }
            },
        )
    }
}




@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ChatBottomInputPartPreview(){
    ChatBottomInputPart()
}
