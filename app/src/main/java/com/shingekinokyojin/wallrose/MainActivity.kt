package com.shingekinokyojin.wallrose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shingekinokyojin.wallrose.navigator.WallRoseNavHost
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

class MainActivity : ComponentActivity() {

    companion object{
        var instance: MainActivity? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContent {
            WallRoseTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WallRoseNavHost()
                }
            }
        }
    }
}

