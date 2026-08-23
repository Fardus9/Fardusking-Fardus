package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PluginRepository(private val pluginDao: PluginDao) {

    val allPluginsFlow: Flow<List<PluginEntity>> = pluginDao.getAllPluginsFlow()

    suspend fun initializeDefaultPluginsIfEmpty() {
        val currentPlugins = pluginDao.getAllPluginsDirect()
        if (currentPlugins.isEmpty()) {
            val defaults = listOf(
                PluginEntity(
                    id = "video_editor",
                    name = "Video Editor Plugin",
                    category = "Video Editing",
                    description = "Splices clips, trims timelines, adjusts play speeds, overlays background tracks, and applies cinema color correction LUTs.",
                    isInstalled = true,
                    isEnabled = true,
                    configJson = """{"resolution":"1080p (FHD)","filter":"Teal & Orange Accent","audio_overlay":"Lo-Fi Synth Beat","bitrate":"12 Mbps"}""",
                    isCustom = false
                ),
                PluginEntity(
                    id = "image_studio",
                    name = "Image Studio Plugin",
                    category = "Graphics & Photos",
                    description = "Crops aspect grids, adjusts exposure/curves, applies styled filters (Vintage Warmth, Cyberpunk Blue, Noir), and handles format transcoding.",
                    isInstalled = true,
                    isEnabled = true,
                    configJson = """{"format":"PNG lossless","style":"Cyberpunk Neon Theme","crop_ratio":"16:9 Cinema","compression":"Optimized"}""",
                    isCustom = false
                ),
                PluginEntity(
                    id = "audio_enhancer",
                    name = "Audio Enhancer Plugin",
                    category = "Audio & Acoustics",
                    description = "Isolates spoken vocals, removes ambient fan/AC hums, boosts low-end sub frequencies, and compresses acoustic dynamics.",
                    isInstalled = true,
                    isEnabled = false,
                    configJson = """{"noise_reduction":"Intense (85%)","bass_boost":"Deep Bass On","vocal_enhancement":"Vivid Dialog","output_gain":"+2.5 dB"}""",
                    isCustom = false
                ),
                PluginEntity(
                    id = "ai_translator",
                    name = "AI Text Translator Plugin",
                    category = "Natural Language AI",
                    description = "Parses textual transcripts, translates dialects into target languages, detects core sentiments, and generates concise outline briefs.",
                    isInstalled = false,
                    isEnabled = false,
                    configJson = """{"target_language":"Bengali (বাংলা)","summary_mode":"Bullet Outline","tone":"Informal & Warm","dialect_variant":"Standard"}""",
                    isCustom = false
                ),
                PluginEntity(
                    id = "data_logger",
                    name = "System Performance Monitor",
                    category = "Developer Tools",
                    description = "Traces sequential pipeline telemetry logs, counts asset processing latency overhead, logs memory usage, and formats results.",
                    isInstalled = true,
                    isEnabled = false,
                    configJson = """{"log_level":"Verbose Trace","track_memory":"Active","auto_export":"JSON Block","verbosity_level":"Max"}""",
                    isCustom = false
                )
            )
            pluginDao.insertPlugins(defaults)
        }
    }

    suspend fun getPluginById(id: String): PluginEntity? = pluginDao.getPluginById(id)

    suspend fun insertPlugin(plugin: PluginEntity) = pluginDao.insertPlugin(plugin)

    suspend fun deletePlugin(plugin: PluginEntity) = pluginDao.deletePlugin(plugin)

    suspend fun toggleEnabled(id: String, isEnabled: Boolean) = pluginDao.updateEnabledState(id, isEnabled)

    suspend fun toggleInstalled(id: String, isInstalled: Boolean) {
        val isEnabled = if (!isInstalled) false else {
            // retain disabled unless it is turned on later
            false
        }
        pluginDao.updateInstallationState(id, isInstalled, isEnabled)
    }

    suspend fun updateConfig(id: String, configJson: String) = pluginDao.updateConfig(id, configJson)
}
