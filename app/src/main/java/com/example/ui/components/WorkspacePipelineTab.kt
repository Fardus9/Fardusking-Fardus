package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkspacePipelineTab(
    plugins: List<PluginEntity>,
    inputFile: String,
    onInputFileChange: (String) -> Unit,
    inputType: String,
    onInputTypeChange: (String) -> Unit,
    logs: List<LogEntry>,
    pipelineStatus: PipelineStatus,
    currentPluginId: String?,
    progress: Float,
    outputAsset: OutputAsset?,
    onRunPipeline: () -> Unit,
    onResetPipeline: () -> Unit
) {
    val activePlugins = remember(plugins) {
        plugins.filter { it.isInstalled && it.isEnabled }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Simulation Workspace",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Compose inputs, active modules, and compile a sequential run.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Part 1: Input Setup Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = SlateSurface,
                contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configure Mock Pipeline Input Payload",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input File Name Input
                OutlinedTextField(
                    value = inputFile,
                    onValueChange = onInputFileChange,
                    label = { Text("Mock Asset File Name") },
                    placeholder = { Text("e.g. vlog_vienna_trip.mov") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pipeline_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedLabelColor = CyanPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    enabled = pipelineStatus != PipelineStatus.PROCESSING
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Format Selector (Chips)
                Text(
                    text = "Input Asset Media Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val mediaTypes = listOf("Video", "Image", "Audio", "Text")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mediaTypes.forEach { type ->
                        val isSelected = inputType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { onInputTypeChange(type) },
                            label = { Text(type, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color(0xFF12141C),
                                containerColor = SlateBorder.copy(alpha = 0.5f),
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = SlateBorder,
                                selectedBorderColor = CyanPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = pipelineStatus != PipelineStatus.PROCESSING,
                            modifier = Modifier.testTag("media_type_chip_$type")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Part 2: Active Pipeline Modules Flow Chart
        Text(
            text = "Pipeline Topology",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (activePlugins.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Empty Pipeline",
                        tint = AccentAmber,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active plugins in modular pipeline stack.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Go to the 'Plugins' tab to install and enable components.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        } else {
            // Horizontal chain of active plugins
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        // Input Payload Node
                        PipelinePayloadNode(
                            label = "Input File",
                            subtext = inputType,
                            icon = when (inputType) {
                                "Video" -> Icons.Default.PlayArrow
                                "Image" -> Icons.Default.Add
                                "Audio" -> Icons.Default.Settings
                                else -> Icons.Default.Add
                            },
                            color = CyanPrimaryDark
                        )
                    }

                    itemsIndexed(activePlugins, key = { _, item -> item.id }) { index, plugin ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow Connect",
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val isCurrent = currentPluginId == plugin.id
                            val isCompleted = currentPluginId != null && 
                                activePlugins.indexOfFirst { it.id == currentPluginId } > index

                            val nodeColor = when {
                                isCurrent -> CyanPrimary
                                isCompleted -> SoftGreen
                                else -> SlateBorder
                            }

                            PipelineModuleNode(
                                plugin = plugin,
                                borderColor = nodeColor,
                                isCurrent = isCurrent,
                                isCompleted = isCompleted
                            )
                        }
                    }

                    item {
                        // Output Node
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow Connect",
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val isSuccess = pipelineStatus == PipelineStatus.SUCCESS
                            val outputColor = if (isSuccess) SoftGreen else SlateBorder

                            PipelinePayloadNode(
                                label = "Generated Asset",
                                subtext = if (isSuccess) "COMPILED" else "WAITING",
                                icon = Icons.Default.Check,
                                color = outputColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Part 3: Console Output Terminal screen
        Text(
            text = "Modular Pipeline Compiler Console",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TerminalLogsView(
            logs = logs,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // Progress bar if compilation is active
        if (pipelineStatus == PipelineStatus.PROCESSING) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyanPrimary,
                    trackColor = SlateBorder
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = CyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Part 4: Run / Reset Workspace Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRunPipeline,
                enabled = pipelineStatus != PipelineStatus.PROCESSING && activePlugins.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("run_pipeline_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF12141C),
                    disabledContainerColor = SlateBorder,
                    disabledContentColor = TextSecondary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Run Icon"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("RUN WORKSPACE", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }

            OutlinedButton(
                onClick = onResetPipeline,
                enabled = pipelineStatus != PipelineStatus.IDLE && pipelineStatus != PipelineStatus.PROCESSING,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("reset_pipeline_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary,
                    disabledContentColor = TextSecondary.copy(alpha = 0.2f)
                ),
                border = BorderStroke(
                    1.dp, 
                    if (pipelineStatus != PipelineStatus.IDLE && pipelineStatus != PipelineStatus.PROCESSING) TextSecondary else SlateBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Icon"
                )
            }
        }

        // Part 5: Processed Transformed Output Asset Detail card
        AnimatedVisibility(
            visible = pipelineStatus == PipelineStatus.SUCCESS && outputAsset != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (outputAsset != null) {
                Spacer(modifier = Modifier.height(20.dp))
                OutputAssetCard(asset = outputAsset)
            }
        }
    }
}

@Composable
fun PipelinePayloadNode(
    label: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF12141C),
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextPrimary
            )
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PipelineModuleNode(
    plugin: PluginEntity,
    borderColor: Color,
    isCurrent: Boolean,
    isCompleted: Boolean
) {
    val highlightBg = if (isCurrent) CyanPrimary.copy(alpha = 0.08f) else Color(0xFF12141C)
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .border(
                border = BorderStroke(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = borderColor
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = highlightBg,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = plugin.category.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) CyanPrimary else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Step Completed",
                        tint = SoftGreen,
                        modifier = Modifier.size(12.dp)
                    )
                } else if (isCurrent) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.5.dp,
                        color = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plugin.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextPrimary
            )

            // Show miniature indication of key custom variable
            val configSnippet = remember(plugin.configJson) {
                try {
                    val json = JSONObject(plugin.configJson)
                    val firstKey = json.keys().asSequence().firstOrNull()
                    if (firstKey != null) {
                        "$firstKey: ${json.optString(firstKey)}"
                    } else ""
                } catch (e: Exception) { "" }
            }

            if (configSnippet.isNotBlank()) {
                Text(
                    text = configSnippet,
                    fontSize = 8.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun OutputAssetCard(asset: OutputAsset) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftGreen, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SlateSurface,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SoftGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Compiled Output Generated",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGreen
                    )
                    Text(
                        text = "Assets have been packaged into single modular output.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // File Info Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0C14))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FILE NAME",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = asset.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "PACK SIZE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = asset.size,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LATENCY",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "${asset.processingTimeMs} ms",
                            fontSize = 12.sp,
                            color = CyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ACTIVE STACK MODULES",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "${asset.activeSteps.size} Plugins Applied",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata entries
            Text(
                text = "Compiled Output Payload Specifications",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                asset.metadata.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = key,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = value,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Divider(color = SlateBorder.copy(alpha = 0.4f))
                }
            }
        }
    }
}
