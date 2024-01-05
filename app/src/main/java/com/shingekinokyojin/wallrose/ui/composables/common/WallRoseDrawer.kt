package com.shingekinokyojin.wallrose.ui.composables.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig.ROUTE_CHAT
import com.shingekinokyojin.wallrose.config.RouteConfig.ROUTE_PROFILE
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import kotlinx.coroutines.launch

private data class DrawerTuple(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
)

private val navData = listOf(
    R.string.tabbar_chat to R.drawable.tabbar_chat to ROUTE_CHAT,
    R.string.tabbar_profile to R.drawable.tabbar_profile to ROUTE_PROFILE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallRoseDrawer(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    WallRoseTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
            )

            navData.forEach {
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                )
                WallRoseDrawerItem(
                    title = it.first.first,
                    icon = it.first.second,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    val currentDestination = navController.currentDestination
                    if (currentDestination?.route != it.second) {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(it.second) {
                            launchSingleTop = true  // 避免在栈顶重复加载相同界面
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun WallRoseDrawerItem(
    modifier: Modifier = Modifier,
    title: Int,
    icon: Int,
    onClick: () -> Unit
) {
    WallRoseTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    modifier = Modifier
                        .weight(1f)
                        .size(20.dp)
                )


                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .height(25.dp)
                        .weight(3f)
                        .padding(start = 10.dp)
                        .clickable(onClick = onClick),
                    textAlign = TextAlign.Center,
                )
            }


            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewWallRoseDrawer() {
    WallRoseDrawer(
        navController = NavController(LocalContext.current),
        drawerState = DrawerState(DrawerValue.Closed)
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewWallRoseDrawerItem() {
    WallRoseDrawerItem(
        title = R.string.tabbar_chat,
        icon = R.drawable.tabbar_chat
    ) {
        {}
    }
}