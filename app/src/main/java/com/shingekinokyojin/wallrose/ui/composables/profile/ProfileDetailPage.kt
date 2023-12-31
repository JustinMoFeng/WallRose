package com.shingekinokyojin.wallrose.ui.composables.profile

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    url:String
) {
    WallRoseTheme {
        Scaffold(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary),
            topBar = {
                WallRoseDetailAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    title = "个人信息",
                    onLeftClick = {
                        navController.popBackStack()
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProfileDetailBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it),
                    navController = navController,
                    userViewModel = userViewModel,
                    url = url
                )
            }
        }
    }
}



@Composable
fun ProfileDetailBody(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    url:String
) {
    WallRoseTheme {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        val selectImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { userViewModel.uploadImage(context, it) }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(text = "确认退出")
                },
                text = {
                    Text("您确定要退出登录吗？")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            SharedPreferencesManager.deleteToken()
                            navController.navigate(RouteConfig.ROUTE_LOGIN)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "确认",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("取消",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                textContentColor = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileDetailItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectImageLauncher.launch("image/*")
                    },
                label = "头像",
                imageValue = if(userViewModel.myAvatarUrl=="") "" else "$url${userViewModel.myAvatarUrl}"
            )
            Spacer(modifier = Modifier.height(15.dp))
            ProfileDetailItem(
                modifier = Modifier
                    .fillMaxWidth(),
                label = "用户名",
                content = userViewModel.myUsername,
                readOnly = true
            )
            ProfileDetailItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        userViewModel.reviseUserInfo = "昵称"
                        navController.navigate(RouteConfig.ROUTE_PROFILE_MODIFICATION)
                    },
                label = "昵称",
                content = userViewModel.myNickname
            )
            Spacer(modifier = Modifier.height(15.dp))
            ProfileDetailItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        userViewModel.reviseUserInfo = "安全"
                        navController.navigate(RouteConfig.ROUTE_PROFILE_MODIFICATION)
                    },
                label = "安全",
                content = "",
                isSecurity = true
            )

            Spacer(modifier = Modifier.weight(0.7f))
            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = "退出登录",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(vertical = 5.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))
        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileDetailItem(
    modifier: Modifier = Modifier,
    label: String,
    content: String = "",
    imageValue:String = "",
    readOnly: Boolean = false,
    isSecurity: Boolean = false
){
    Log.d("ProfileDetailItem", "ProfileDetailItem: $imageValue")
    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier
                    .wrapContentWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.weight(1f))

            if (content!="") {
                Text(
                    text = content,
                    modifier = Modifier
                        .wrapContentWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else if (imageValue != "") {
                Image(
                    painter = rememberImagePainter(imageValue),
                    contentDescription = "right arrow",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (!isSecurity){
                Image(
                    painter = painterResource(id = R.drawable.profile_user_example),
                    contentDescription = "right arrow",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            if (readOnly) {
                Spacer(modifier = Modifier.width(25.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_detail_rightarrow),
                    contentDescription = "right arrow",
                    modifier = Modifier
                        .height(15.dp)
                        .padding(start = 10.dp)
                )
            }

        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
//@Composable
//fun ProfileDetailPagePreview() {
//    ProfileDetailPage(navController = NavController(LocalContext.current))
//}
//
//
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
//@Composable
//fun ProfileDetailBodyPreview() {
//    ProfileDetailBody(
//        navController = NavController(LocalContext.current)
//    )
//}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ProfileDetailItemPreview() {
    ProfileDetailItem(
        label = "昵称",
        content = "小明"
    )
}