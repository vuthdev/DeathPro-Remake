package org.firestorm.deathproRemake.common.constants

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.DeathproRemake

object GhostKeys {
    lateinit var REMAINING_SECONDS: NamespacedKey private set
    lateinit var DEATH_WORLD: NamespacedKey private set
    lateinit var DEATH_X: NamespacedKey private set
    lateinit var DEATH_Y: NamespacedKey private set
    lateinit var DEATH_Z: NamespacedKey private set
    lateinit var RESPAWN_WORLD: NamespacedKey private set
    lateinit var RESPAWN_X: NamespacedKey private set
    lateinit var RESPAWN_Y: NamespacedKey private set
    lateinit var RESPAWN_Z: NamespacedKey private set

    fun init(plugin: JavaPlugin) {
        REMAINING_SECONDS = NamespacedKey(plugin, "ghost_remaining_seconds")
        DEATH_WORLD = NamespacedKey(plugin, "ghost_death_world")
        DEATH_X = NamespacedKey(plugin, "ghost_death_x")
        DEATH_Y = NamespacedKey(plugin, "ghost_death_y")
        DEATH_Z = NamespacedKey(plugin, "ghost_death_z")
        RESPAWN_WORLD = NamespacedKey(plugin, "ghost_respawn_world")
        RESPAWN_X = NamespacedKey(plugin, "ghost_respawn_x")
        RESPAWN_Y = NamespacedKey(plugin, "ghost_respawn_y")
        RESPAWN_Z = NamespacedKey(plugin, "ghost_respawn_z")
    }
}