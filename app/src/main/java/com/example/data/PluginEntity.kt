package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plugins")
data class PluginEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val isInstalled: Boolean = true,
    val isEnabled: Boolean = false,
    val configJson: String = "{}",
    val isCustom: Boolean = false,
    val customRules: String = ""
)
