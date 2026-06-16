package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration

abstract class ConfigSection(
    protected val config: FileConfiguration,
    private val path: String,
) {
    protected fun string(key: String, default: String = "") = config.getString("$path.$key", default) ?: default
    protected fun int(key: String, default: Int = 0) = config.getInt("$path.$key", default)
    protected fun boolean(key: String, default: Boolean = false) = config.getBoolean("$path.$key", default)
    protected fun stringList(key: String): List<String> = config.getStringList("$path.$key")
}