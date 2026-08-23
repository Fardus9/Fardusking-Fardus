package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LogEntry
import com.example.data.LogType
import com.example.ui.theme.*

@Composable
fun TerminalLogsView(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Automatically scroll to bottom when new logs arrive
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0C14))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TERMINAL READY",
                        color = TextSecondary.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Press 'RUN WORKSPACE' to execute plugin pipeline",
                        color = TextSecondary.copy(alpha = 0.3f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Color label based on LogType
                        val color = when (log.type) {
                            LogType.SYSTEM -> AccentAmber
                            LogType.INFO -> CyanPrimary
                            LogType.SUCCESS -> SoftGreen
                            LogType.WARNING -> SoftRed
                            LogType.TERMINAL -> TextPrimary.copy(alpha = 0.7f)
                        }

                        val prefix = when (log.type) {
                            LogType.SYSTEM -> "⚡ "
                            LogType.INFO -> "ℹ️ "
                            LogType.SUCCESS -> "✔ "
                            LogType.WARNING -> "⚠️ "
                            LogType.TERMINAL -> "  "
                        }

                        Text(
                            text = "$prefix${log.message}",
                            color = color,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
