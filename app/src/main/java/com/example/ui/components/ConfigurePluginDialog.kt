package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PluginEntity
import com.example.ui.theme.*
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurePluginDialog(
    plugin: PluginEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // Parse the JSON
    val initialConfigs = remember(plugin.configJson) {
        val map = mutableStateMapOf<String, String>()
        try {
            val json = JSONObject(plugin.configJson)
            json.keys().forEach { key ->
                map[key] = json.optString(key)
            }
        } catch (e: Exception) {
            // fallback
        }
        map
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SlateSurface,
                contentColor = TextPrimary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Config Icon",
                            tint = CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Configure Plugin",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = plugin.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Configure parameters passed to the modular pipeline during execution.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable fields
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (initialConfigs.isEmpty()) {
                        Text(
                            text = "This plugin requires no custom parameters.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        initialConfigs.forEach { (key, value) ->
                            // Nicely format key label from snake_case or kebab-case to readable title
                            val label = key.replace("_", " ").replace("-", " ")
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
                            )

                            OutlinedTextField(
                                value = value,
                                onValueChange = { initialConfigs[key] = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("config_${key}_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = SlateBorder,
                                    focusedLabelColor = CyanPrimary,
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            // Re-bundle into JSON
                            val json = JSONObject()
                            initialConfigs.forEach { (k, v) ->
                                json.put(k, v)
                            }
                            onSave(json.toString())
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color(0xFF12141C)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_config_button")
                    ) {
                        Text("Apply Config", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
