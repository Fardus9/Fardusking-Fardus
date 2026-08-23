package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                
                // Initialize Room DB & Repository
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { PluginRepository(database.pluginDao()) }
                
                // Create WorkspaceViewModel
                val viewModel: WorkspaceViewModel = viewModel(
                    factory = WorkspaceViewModelFactory(
                        application = application,
                        repository = repository
                    )
                )

                // Render main content
                PluginHubApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginHubApp(viewModel: WorkspaceViewModel) {
    val plugins by viewModel.pluginsState.collectAsStateWithLifecycle()
    val logs by viewModel.pipelineLogs.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableStateOf(0) } // 0 = Workspace, 1 = Plugins Catalog
    var activeConfigPlugin by remember { mutableStateOf<PluginEntity?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val activeCount = remember(plugins) { plugins.count { it.isInstalled && it.isEnabled } }
    val totalCount = remember(plugins) { plugins.count() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Console App Icon",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Plugin Hub",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "প্লাগইন হাব • Stack Controller",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick Status Indicators
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateBorder.copy(alpha = 0.4f))
                                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (activeCount > 0) SoftGreen else SoftRed)
                                )
                                Text(
                                    text = "$activeCount/$totalCount Active",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkCharcoal,
                        titleContentColor = TextPrimary
                    )
                )
                Divider(color = SlateBorder)
            }
        },
        bottomBar = {
            Column {
                Divider(color = SlateBorder)
                NavigationBar(
                    containerColor = DarkCharcoal,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Workspace Tab"
                            )
                        },
                        label = { Text("Workspace", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF12141C),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_workspace_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Plugins Tab"
                            )
                        },
                        label = { Text("Plugins", fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF12141C),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_plugins_tab")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> {
                    WorkspacePipelineTab(
                        plugins = plugins,
                        inputFile = viewModel.pipelineInputFile,
                        onInputFileChange = { viewModel.pipelineInputFile = it },
                        inputType = viewModel.pipelineInputType,
                        onInputTypeChange = { viewModel.pipelineInputType = it },
                        logs = logs,
                        pipelineStatus = viewModel.pipelineStatus,
                        currentPluginId = viewModel.currentProcessingPluginId,
                        progress = viewModel.pipelineProgress,
                        outputAsset = viewModel.finalOutputAsset,
                        onRunPipeline = { viewModel.runPipeline() },
                        onResetPipeline = { viewModel.resetPipeline() }
                    )
                }
                1 -> {
                    PluginCatalogTab(
                        plugins = plugins,
                        onToggleEnable = { id, active -> viewModel.togglePluginEnabled(id, active) },
                        onToggleInstall = { id, install -> viewModel.togglePluginInstallation(id, install) },
                        onDeleteCustom = { plugin -> viewModel.deleteCustomPlugin(plugin) },
                        onConfigurePlugin = { plugin -> activeConfigPlugin = plugin },
                        onCreateNewPluginClicked = { showCreateDialog = true }
                    )
                }
            }
        }
    }

    // Configure dialog
    activeConfigPlugin?.let { plugin ->
        ConfigurePluginDialog(
            plugin = plugin,
            onDismiss = { activeConfigPlugin = null },
            onSave = { updatedJson ->
                viewModel.updatePluginConfiguration(plugin.id, updatedJson)
            }
        )
    }

    // Create custom plugin dialog
    if (showCreateDialog) {
        CreatePluginDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, category, description, rules, configs ->
                viewModel.createCustomPlugin(name, category, description, rules, configs)
            }
        )
    }
}
