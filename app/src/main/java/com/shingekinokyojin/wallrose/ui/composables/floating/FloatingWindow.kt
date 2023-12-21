package com.shingekinokyojin.wallrose.ui.composables.floating

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyFloatingContent(
    modifier: Modifier = Modifier
) {
    // 一个圆点，size 为 25.dp
    Spacer(
        modifier = modifier
            .size(25.dp)
            .background(color = MaterialTheme.colorScheme.background, CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )

}