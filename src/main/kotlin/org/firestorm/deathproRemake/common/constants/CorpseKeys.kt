package org.firestorm.deathproRemake.common.constants

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

object CorpseKeys {
    lateinit var REMAINING_SECONDS: NamespacedKey private set

    fun init(plugin: JavaPlugin) {
        REMAINING_SECONDS = NamespacedKey(plugin, "corpse_remaining_seconds")
    }
}