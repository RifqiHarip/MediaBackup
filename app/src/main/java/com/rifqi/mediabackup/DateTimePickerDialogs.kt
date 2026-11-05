package com.rifqi.mediabackup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelectionDialog(
    title: String,
    initialTimestamp: Long,
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialTimestamp
    )
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialTimestamp } }
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    if (!showTimePicker) {
        // Step 1: Pick Date
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { showTimePicker = true },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Next (Select Time)")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                DatePicker(state = datePickerState)
            }
        }
    } else {
        // Step 2: Pick Time
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val resultCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = selectedDateMillis
                    }
                    val finalCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, resultCal.get(Calendar.YEAR))
                        set(Calendar.MONTH, resultCal.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, resultCal.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateTimeSelected(finalCal.timeInMillis)
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Back") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}