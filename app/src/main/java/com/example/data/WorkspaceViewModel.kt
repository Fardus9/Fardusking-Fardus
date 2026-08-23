package com.example.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class PipelineStatus {
    IDLE, PROCESSING, SUCCESS, FAILED
}

data class LogEntry(
    val id: String,
    val message: String,
    val type: LogType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogType {
    SYSTEM, INFO, SUCCESS, WARNING, TERMINAL
}

data class OutputAsset(
    val name: String,
    val type: String,
    val size: String,
    val processingTimeMs: Long,
    val activeSteps: List<String>,
    val metadata: Map<String, String>
)

class WorkspaceViewModel(
    application: Application,
    private val repository: PluginRepository
) : AndroidViewModel(application) {

    // Fetch all plugins reactively from DB
    val pluginsState: StateFlow<List<PluginEntity>> = repository.allPluginsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Pipeline State
    var pipelineInputFile by mutableStateOf("raw_vlog_footage.mov")
    var pipelineInputType by mutableStateOf("Video") // Video, Image, Audio, Text

    private val _pipelineLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val pipelineLogs: StateFlow<List<LogEntry>> = _pipelineLogs.asStateFlow()

    var pipelineStatus by mutableStateOf(PipelineStatus.IDLE)
        private set

    var currentProcessingPluginId by mutableStateOf<String?>(null)
        private set

    var pipelineProgress by mutableStateOf(0f)
        private set

    var finalOutputAsset by mutableStateOf<OutputAsset?>(null)
        private set

    init {
        viewModelScope.launch {
            // Prepopulate defaults if DB is empty
            repository.initializeDefaultPluginsIfEmpty()
        }
    }

    // Toggle plugin installation state
    fun togglePluginInstallation(id: String, isInstalled: Boolean) {
        viewModelScope.launch {
            repository.toggleInstalled(id, isInstalled)
            addLog("Plugin '${id}' ${if (isInstalled) "installed" else "uninstalled"}.", LogType.SYSTEM)
        }
    }

    // Toggle plugin enabled state
    fun togglePluginEnabled(id: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleEnabled(id, isEnabled)
            addLog("Plugin '${id}' ${if (isEnabled) "enabled" else "disabled"}.", LogType.SYSTEM)
        }
    }

    // Update custom configuration json
    fun updatePluginConfiguration(id: String, configJson: String) {
        viewModelScope.launch {
            repository.updateConfig(id, configJson)
            addLog("Configuration updated for plugin '${id}'.", LogType.SYSTEM)
        }
    }

    // Create a new custom plugin
    fun createCustomPlugin(
        name: String,
        category: String,
        description: String,
        customRules: String,
        defaultConfigMap: Map<String, String>
    ) {
        viewModelScope.launch {
            val uniqueId = "custom_${System.currentTimeMillis()}"
            val configJson = JSONObject(defaultConfigMap).toString()
            val newPlugin = PluginEntity(
                id = uniqueId,
                name = name,
                category = category,
                description = description,
                isInstalled = true,
                isEnabled = true,
                configJson = configJson,
                isCustom = true,
                customRules = customRules
            )
            repository.insertPlugin(newPlugin)
            addLog("Custom plugin '${name}' created and activated.", LogType.SUCCESS)
        }
    }

    // Delete a custom plugin
    fun deleteCustomPlugin(plugin: PluginEntity) {
        viewModelScope.launch {
            if (plugin.isCustom) {
                repository.deletePlugin(plugin)
                addLog("Custom plugin '${plugin.name}' deleted permanently.", LogType.SYSTEM)
            }
        }
    }

    // Helper to log entries
    private fun addLog(message: String, type: LogType) {
        val newEntry = LogEntry(
            id = java.util.UUID.randomUUID().toString(),
            message = message,
            type = type
        )
        _pipelineLogs.value = _pipelineLogs.value + newEntry
    }

    // Reset pipeline state
    fun resetPipeline() {
        pipelineStatus = PipelineStatus.IDLE
        _pipelineLogs.value = emptyList()
        pipelineProgress = 0f
        currentProcessingPluginId = null
        finalOutputAsset = null
    }

    // Execute sequential modular pipeline
    fun runPipeline() {
        if (pipelineStatus == PipelineStatus.PROCESSING) return

        viewModelScope.launch {
            pipelineStatus = PipelineStatus.PROCESSING
            _pipelineLogs.value = emptyList()
            pipelineProgress = 0.05f
            finalOutputAsset = null

            addLog("[INIT] Booting Modular Plugin System Core Engine...", LogType.SYSTEM)
            delay(600)

            val activePlugins = pluginsState.value.filter { it.isInstalled && it.isEnabled }
            
            if (activePlugins.isEmpty()) {
                addLog("[ERROR] No active plugins detected. Please install and enable at least one plugin to compile a workspace pipeline.", LogType.WARNING)
                pipelineStatus = PipelineStatus.FAILED
                pipelineProgress = 1f
                return@launch
            }

            addLog("[PIPELINE] Input Registered: '$pipelineInputFile' ($pipelineInputType)", LogType.INFO)
            addLog("[PIPELINE] Sequential modular stack verified. Active Plugin Count: ${activePlugins.size}", LogType.INFO)
            delay(800)

            var currentStep = 0
            val totalSteps = activePlugins.size
            val stepSize = 0.90f / totalSteps

            val finalMetadata = mutableMapOf<String, String>()
            finalMetadata["Source File"] = pipelineInputFile
            finalMetadata["Source Format"] = pipelineInputType

            val stepsRun = mutableListOf<String>()

            for (plugin in activePlugins) {
                currentProcessingPluginId = plugin.id
                addLog("[CORE] Routing payload to: ${plugin.name} [ID: ${plugin.id}]", LogType.SYSTEM)
                delay(400)

                stepsRun.add(plugin.name)

                // Try to parse configurations to include in logs
                val configs = try {
                    val jsonObj = JSONObject(plugin.configJson)
                    val map = mutableMapOf<String, String>()
                    jsonObj.keys().forEach { key ->
                        map[key] = jsonObj.optString(key)
                    }
                    map
                } catch (e: Exception) {
                    emptyMap()
                }

                addLog("  > Running modular logic with configs: $configs", LogType.TERMINAL)
                delay(600)

                // Simulated inner processing phases per plugin
                addLog("  > Applying algorithm parameters...", LogType.TERMINAL)
                var stepProgress = 0f
                while (stepProgress < 1.0f) {
                    stepProgress += 0.35f
                    pipelineProgress = 0.05f + (currentStep * stepSize) + (stepProgress.coerceAtMost(1f) * stepSize * 0.8f)
                    delay(300)
                }

                // Apply processing outcomes based on plugin types
                when (plugin.id) {
                    "video_editor" -> {
                        val resolution = configs["resolution"] ?: "1080p"
                        val filter = configs["filter"] ?: "Teal & Orange"
                        val audio = configs["audio_overlay"] ?: "None"
                        addLog("[SUCCESS] ${plugin.name} completed! Output rendered at $resolution with color grade: '$filter' and soundtrack: '$audio'.", LogType.SUCCESS)
                        finalMetadata["Resolution"] = resolution
                        finalMetadata["Color Grading"] = filter
                        finalMetadata["Soundtrack Overlay"] = audio
                    }
                    "image_studio" -> {
                        val format = configs["format"] ?: "PNG"
                        val style = configs["style"] ?: "Default"
                        val crop = configs["crop_ratio"] ?: "Original"
                        addLog("[SUCCESS] ${plugin.name} completed! Transcoded to $format, dynamic range scaled, color space style: '$style', crop ratio: $crop.", LogType.SUCCESS)
                        finalMetadata["Output Format"] = format
                        finalMetadata["Graphic Theme"] = style
                        finalMetadata["Aspect Ratio"] = crop
                    }
                    "audio_enhancer" -> {
                        val reduction = configs["noise_reduction"] ?: "Off"
                        val boost = configs["bass_boost"] ?: "Disabled"
                        addLog("[SUCCESS] ${plugin.name} completed! Noise cancellation is set to $reduction, bass amplification is $boost. Midtones cleaned.", LogType.SUCCESS)
                        finalMetadata["Noise Suppression"] = reduction
                        finalMetadata["Bass Amplification"] = boost
                    }
                    "ai_translator" -> {
                        val targetLang = configs["target_language"] ?: "English"
                        val mode = configs["summary_mode"] ?: "Brief"
                        addLog("[SUCCESS] ${plugin.name} completed! Auto-transcribing dialogue. Translating dialect safely to $targetLang ($mode summary format).", LogType.SUCCESS)
                        finalMetadata["Target Dialect"] = targetLang
                        finalMetadata["AI Summary"] = mode
                    }
                    "data_logger" -> {
                        val level = configs["log_level"] ?: "Info"
                        val trackMem = configs["track_memory"] ?: "Disabled"
                        addLog("[SUCCESS] ${plugin.name} completed! Sequential trace level set to: $level. Memory tracking is $trackMem. Compilation check passes.", LogType.SUCCESS)
                        finalMetadata["Telemetry Trace"] = level
                        finalMetadata["Memory Diagnostics"] = trackMem
                    }
                    else -> {
                        // Custom plugin behavior
                        val rules = plugin.customRules.ifBlank { "Simple simulation pass" }
                        addLog("[SUCCESS] ${plugin.name} (Custom Module) completed! Executed rules: '$rules'. Custom parameter state injected.", LogType.SUCCESS)
                        configs.forEach { (key, value) ->
                            finalMetadata["[Custom] $key"] = value
                        }
                        if (plugin.customRules.isNotBlank()) {
                            finalMetadata["[Custom Rules]"] = plugin.customRules
                        }
                    }
                }
                currentStep++
                pipelineProgress = 0.05f + (currentStep * stepSize)
                delay(500)
            }

            // Finish pipeline
            currentProcessingPluginId = null
            pipelineProgress = 1.0f
            pipelineStatus = PipelineStatus.SUCCESS
            addLog("[COMPLETE] Modular compiling completed safely. Packing output workspace.", LogType.SUCCESS)

            // Compose output asset details
            val origNameWithoutExt = pipelineInputFile.substringBeforeLast(".")
            val origExt = pipelineInputFile.substringAfterLast(".", "")
            val finalExt = when {
                finalMetadata.containsKey("Resolution") -> "mp4"
                finalMetadata.containsKey("Output Format") -> {
                    val fmt = finalMetadata["Output Format"]?.lowercase() ?: ""
                    if (fmt.contains("png")) "png" else if (fmt.contains("jpeg") || fmt.contains("jpg")) "jpg" else "webp"
                }
                pipelineInputType == "Audio" -> "wav"
                pipelineInputType == "Text" -> "txt"
                else -> origExt.ifBlank { "dat" }
            }

            val finalAssetName = "${origNameWithoutExt}_plugified_output.$finalExt"
            val totalLatency = (activePlugins.size * 1800 + (100..400).random()).toLong()
            val simulatedSize = when (pipelineInputType) {
                "Video" -> "${(15..45).random()} MB"
                "Image" -> "${(1..5).random()} MB"
                "Audio" -> "${(2..8).random()} MB"
                else -> "${(5..15).random()} KB"
            }

            finalOutputAsset = OutputAsset(
                name = finalAssetName,
                type = pipelineInputType,
                size = simulatedSize,
                processingTimeMs = totalLatency,
                activeSteps = stepsRun,
                metadata = finalMetadata
            )
            addLog("[SYSTEM] Asset generated: '$finalAssetName' [Size: $simulatedSize, Render latency: ${totalLatency}ms]", LogType.SUCCESS)
        }
    }
}

class WorkspaceViewModelFactory(
    private val application: Application,
    private val repository: PluginRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkspaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkspaceViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
