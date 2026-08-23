package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePluginDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, category: String, description: String, rules: String, configs: Map<String, String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Custom Processing") }
    var description by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    // Dynamic config keys
    var configKey1 by remember { mutableStateOf("watermark") }
    var configVal1 by remember { mutableStateOf("[Plugify]") }

    var configKey2 by remember { mutableStateOf("render_speed") }
    var configVal2 by remember { mutableStateOf("Fast") }

    var showError by remember { mutableStateOf(false) }

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
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Icon",
                            tint = CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Develop Custom Plugin",
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

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Plugin Name
                    Text("Plugin Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; if(it.isNotBlank()) showError = false },
                        placeholder = { Text("e.g., Frame Optimizer Pro", color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, top = 4.dp)
                            .testTag("plugin_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Category
                    Text("Module Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, top = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Description
                    Text("Module Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe what this plugin does in the sequential flow...", color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, top = 4.dp)
                            .testTag("plugin_desc_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3
                    )

                    // Execution Rules
                    Text("Modular Exec Rules / Actions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = rules,
                        onValueChange = { rules = it },
                        placeholder = { Text("e.g., [Upper-case all text inputs], [Inject timestamp watermark]", color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, top = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Custom Config Parameters
                    Text(
                        text = "Config Parameters (Key-Value Setup)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = configKey1,
                            onValueChange = { configKey1 = it },
                            placeholder = { Text("Key name") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = configVal1,
                            onValueChange = { configVal1 = it },
                            placeholder = { Text("Default value") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = configKey2,
                            onValueChange = { configKey2 = it },
                            placeholder = { Text("Key name") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = configVal2,
                            onValueChange = { configVal2 = it },
                            placeholder = { Text("Default value") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    if (showError) {
                        Text(
                            text = "Please enter a valid plugin name.",
                            color = SoftRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            if (name.isBlank()) {
                                showError = true
                            } else {
                                val configs = mutableMapOf<String, String>()
                                if (configKey1.isNotBlank()) {
                                    configs[configKey1.trim().lowercase().replace(" ", "_")] = configVal1
                                }
                                if (configKey2.isNotBlank()) {
                                    configs[configKey2.trim().lowercase().replace(" ", "_")] = configVal2
                                }
                                onCreate(name.trim(), category.trim(), description.trim(), rules.trim(), configs)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color(0xFF12141C)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_plugin_button")
                    ) {
                        Text("Build Plugin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
