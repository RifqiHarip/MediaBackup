package com.rifqi.mediabackup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rifqi.mediabackup.ui.theme.BlackRedTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlackRedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MediaSyncScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSyncScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    var destUri by remember { mutableStateOf<Uri?>(null) }

    // Default range: Start of today to End of today
    val now = System.currentTimeMillis()
    var startTimestamp by remember { mutableLongStateOf(now - 86400000L) } // 24 hours ago
    var endTimestamp by remember { mutableLongStateOf(now) }

    var activeDialog by remember { mutableStateOf<DialogType?>(null) }

    var isTransferring by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Idle") }
    var progress by remember { mutableFloatStateOf(0f) }

    val dateTimeFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // SAF Launchers
    val sourcePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            sourceUri = it
        }
    }

    val destPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            destUri = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Adds automatic spacing above content for system status bar
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Media Sync (USB / Device)",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Source & Destination Selectors
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { sourcePicker.launch(null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Source: ${sourceUri?.path ?: "Select Folder"}")
                }

                Button(
                    onClick = { destPicker.launch(null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Destination: ${destUri?.path ?: "Select Folder"}")
                }
            }
        }

        // Date Range Selection Section
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Time Filter Range", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { activeDialog = DialogType.START },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("From", style = MaterialTheme.typography.labelSmall)
                            Text(dateTimeFormatter.format(Date(startTimestamp)), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    OutlinedButton(
                        onClick = { activeDialog = DialogType.END },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("To", style = MaterialTheme.typography.labelSmall)
                            Text(dateTimeFormatter.format(Date(endTimestamp)), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Transfer Action Button
        Button(
            enabled = sourceUri != null && destUri != null && !isTransferring,
            onClick = {
                isTransferring = true
                scope.launch {
                    val engine = MediaSyncEngine(context)
                    engine.syncMedia(sourceUri!!, destUri!!, startTimestamp, endTimestamp) { copied, total, current ->
                        statusText = if (current == "Done") "Transfer complete!" else "Copying ($copied/$total): $current"
                        progress = if (total > 0) copied.toFloat() / total else 0f
                        if (current == "Done") isTransferring = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (isTransferring) "Transferring..." else "Start Sync", style = MaterialTheme.typography.titleMedium)
        }

        // Status & Progress
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    // Dialog Triggering
    when (activeDialog) {
        DialogType.START -> {
            DateTimeSelectionDialog(
                title = "Select Start Date & Time",
                initialTimestamp = startTimestamp,
                onDismiss = { activeDialog = null },
                onDateTimeSelected = { selected ->
                    startTimestamp = selected
                    activeDialog = null
                }
            )
        }
        DialogType.END -> {
            DateTimeSelectionDialog(
                title = "Select End Date & Time",
                initialTimestamp = endTimestamp,
                onDismiss = { activeDialog = null },
                onDateTimeSelected = { selected ->
                    endTimestamp = selected
                    activeDialog = null
                }
            )
        }
        null -> {}
    }
}

enum class DialogType { START, END }