package com.ahmad.lyricquest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmad.lyricquest.player.PlayerController
import com.ahmad.lyricquest.ui.theme.LyricQuestTheme
import com.ahmad.lyricquest.ui.theme.Mist

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LyricQuestTheme {
                PlayerScreen()
            }
        }
    }
}

@Composable
fun PlayerScreen() {
    val context = LocalContext.current
    val controller = remember { PlayerController(context) }
    DisposableEffect(Unit) { onDispose { controller.release() } }

    var title by remember { mutableStateOf("No song yet") }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            title = uri.lastPathSegment?.substringAfterLast('/') ?: "Song"
            controller.load(uri)
        }
    }

    val isPlaying by controller.isPlaying.collectAsStateWithLifecycle()
    val position by controller.positionMs.collectAsStateWithLifecycle()
    val duration by controller.durationMs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(
            "${format(position)} / ${format(duration)}",
            style = MaterialTheme.typography.bodyMedium,
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
        Button(
            onClick = { controller.togglePlay() },
            colors = ButtonDefaults.buttonColors(),
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
        Button(
            onClick = { picker.launch(arrayOf("audio/*")) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("Add a song")
        }
    }
}

private fun format(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
