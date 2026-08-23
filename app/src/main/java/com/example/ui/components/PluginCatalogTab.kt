package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PluginEntity
import com.example.ui.theme.*

@Composable
fun PluginCatalogTab(
    plugins: List<PluginEntity>,
    onToggleEnable: (String, Boolean) -> Unit,
    onToggleInstall: (String, Boolean) -> Unit,
    onDeleteCustom: (PluginEntity) -> Unit,
    onConfigurePlugin: (PluginEntity) -> Unit,
    onCreateNewPluginClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Upper banner for creating a custom plugin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Engine Modules",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Manage core and user-built execution blocks.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = onCreateNewPluginClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF12141C)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("create_plugin_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Build Plugin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (plugins.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Empty Catalog",
                        tint = TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No plugins found in system.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // extra padding for safe area
            ) {
                items(plugins, key = { it.id }) { plugin ->
                    PluginItemCard(
                        plugin = plugin,
                        onToggleEnable = { onToggleEnable(plugin.id, it) },
                        onToggleInstall = { onToggleInstall(plugin.id, it) },
                        onDeleteCustom = { onDeleteCustom(plugin) },
                        onConfigurePlugin = { onConfigurePlugin(plugin) }
                    )
                }
            }
        }
    }
}

@Composable
fun PluginItemCard(
    plugin: PluginEntity,
    onToggleEnable: (Boolean) -> Unit,
    onToggleInstall: (Boolean) -> Unit,
    onDeleteCustom: () -> Unit,
    onConfigurePlugin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .testTag("plugin_card_${plugin.id}"),
        colors = CardDefaults.cardColors(
            containerColor = SlateSurface,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = plugin.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Custom vs Core Badge
                        val badgeBg = if (plugin.isCustom) Color(0xFF311B92) else Color(0xFF263238)
                        val badgeText = if (plugin.isCustom) "User Plugin" else "Core Module"
                        val badgeColor = if (plugin.isCustom) CyanPrimary else TextSecondary

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    Text(
                        text = plugin.category,
                        fontSize = 11.sp,
                        color = CyanPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Install/Uninstall Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (plugin.isInstalled) "Installed" else "Available",
                        fontSize = 11.sp,
                        color = if (plugin.isInstalled) SoftGreen else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = plugin.isInstalled,
                        onCheckedChange = onToggleInstall,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SoftGreen,
                            checkedTrackColor = SoftGreen.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SlateBorder
                        ),
                        modifier = Modifier.scale(0.8f).testTag("install_switch_${plugin.id}")
                    )
                }
            }

            // Description
            Text(
                text = plugin.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // If installed, show enabling toggle and config buttons
            if (plugin.isInstalled) {
                Divider(color = SlateBorder, modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Enabled State
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = plugin.isEnabled,
                            onCheckedChange = onToggleEnable,
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyanPrimary,
                                uncheckedColor = TextSecondary,
                                checkmarkColor = Color(0xFF12141C)
                            ),
                            modifier = Modifier.testTag("enable_check_${plugin.id}")
                        )
                        Text(
                            text = if (plugin.isEnabled) "ACTIVE PIPELINE STEP" else "DISABLED STEP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (plugin.isEnabled) CyanPrimary else TextSecondary
                        )
                    }

                    // Configuration Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Configuration Settings Button
                        IconButton(
                            onClick = onConfigurePlugin,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateBorder.copy(alpha = 0.5f))
                                .testTag("configure_button_${plugin.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configure Parameters",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Delete Custom Button
                        if (plugin.isCustom) {
                            IconButton(
                                onClick = onDeleteCustom,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftRed.copy(alpha = 0.15f))
                                    .testTag("delete_button_${plugin.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Custom Module",
                                    tint = SoftRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


