package com.ahmad.lyricquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ahmad.lyricquest.data.Song
import com.ahmad.lyricquest.ui.LibraryScreen
import com.ahmad.lyricquest.ui.PlayerScreen
import com.ahmad.lyricquest.ui.theme.LyricQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LyricQuestTheme {
                var current by remember { mutableStateOf<Song?>(null) }
                val song = current
                if (song == null) {
                    LibraryScreen(onOpen = { current = it })
                } else {
                    PlayerScreen(song = song, onBack = { current = null })
                }
            }
        }
    }
}
