package com.ahmad.lyricquest.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmad.lyricquest.data.Song
import com.ahmad.lyricquest.player.PlayerController
import com.ahmad.lyricquest.ui.theme.Mist

@Composable
fun PlayerScreen(song: Song, onBack: () -> Unit) {
    val context = LocalContext.current
    val controller = remember { PlayerController(context) }
    DisposableEffect(Unit) { onDispose { controller.release() } }
    LaunchedEffect(song.id) { controller.load(Uri.parse(song.uri)) }
    BackHandler(onBack = onBack)

    val isPlaying by controller.isPlaying.collectAsStateWithLifecycle()
    val position by controller.positionMs.collectAsStateWithLifecycle()
    val duration by controller.durationMs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text("Back to songs")
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(song.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                "${format(position)} / ${format(duration)}",
                color = Mist,
                modifier = Modifier.padding(top = 8.dp),
            )
            Slider(
                value = if (duration > 0) position.toFloat() / duration else 0f,
                onValueChange = { controller.seekTo((it * duration).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
            )
            Button(onClick = { controller.togglePlay() }) {
                Text(if (isPlaying) "Pause" else "Play")
            }
        }
    }
}

private fun format(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
