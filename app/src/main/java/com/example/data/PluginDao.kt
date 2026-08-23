package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginDao {
    @Query("SELECT * FROM plugins")
    fun getAllPluginsFlow(): Flow<List<PluginEntity>>

    @Query("SELECT * FROM plugins")
    suspend fun getAllPluginsDirect(): List<PluginEntity>

    @Query("SELECT * FROM plugins WHERE id = :id")
    suspend fun getPluginById(id: String): PluginEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugin(plugin: PluginEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugins(plugins: List<PluginEntity>)

    @Update
    suspend fun updatePlugin(plugin: PluginEntity)

    @Delete
    suspend fun deletePlugin(plugin: PluginEntity)

    @Query("UPDATE plugins SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateEnabledState(id: String, isEnabled: Boolean)

    @Query("UPDATE plugins SET isInstalled = :isInstalled, isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateInstallationState(id: String, isInstalled: Boolean, isEnabled: Boolean)

    @Query("UPDATE plugins SET configJson = :configJson WHERE id = :id")
    suspend fun updateConfig(id: String, configJson: String)
}
