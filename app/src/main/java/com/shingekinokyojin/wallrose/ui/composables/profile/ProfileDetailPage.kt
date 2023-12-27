package com.shingekinokyojin.wallrose.ui.composables.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@Composable
fun ProfileDetailPage() {

}

@Composable
fun ProfileDetailBody() {

}

@Composable
fun ProfileDetailItem(
    modifier: Modifier = Modifier,
    label: String,
    content: String = "",
    imageValue:String = ""
){
    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.secondary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier
                    .wrapContentWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            
            if (content.isNotEmpty()) {
                Text(
                    text = content,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}