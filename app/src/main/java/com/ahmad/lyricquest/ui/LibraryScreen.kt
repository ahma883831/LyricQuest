package com.ahmad.lyricquest.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmad.lyricquest.data.AppDatabase
import com.ahmad.lyricquest.data.Song
import com.ahmad.lyricquest.ui.theme.Mist
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(onOpen: (Song) -> Unit) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.get(context).songDao() }
    val songsFlow = remember { dao.all() }
    val songs by songsFlow.collectAsStateWithLifecycle(emptyList())
    val scope = rememberCoroutineScope()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            scope.launch {
                dao.insert(Song(title = displayName(context, uri), uri = uri.toString()))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Your songs", style = MaterialTheme.typography.headlineMedium)

        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Add your first song to start learning from its lyrics.",
                    color = Mist,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(songs, key = { it.id }) { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpen(song) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            song.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { scope.launch { dao.delete(song) } }) {
                            Text("Remove", color = Mist)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { picker.launch(arrayOf("audio/*")) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add a song")
        }
    }
}

private fun displayName(context: Context, uri: Uri): String {
    context.contentResolver.query(
        uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            if (name != null) return name.substringBeforeLast('.')
        }
    }
    return "Song"
}
