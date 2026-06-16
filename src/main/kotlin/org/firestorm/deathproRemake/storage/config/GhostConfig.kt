package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class CorpseConfig(plugin: FileConfiguration) {
    private val config = plugin.config

    val ghost = GhostConfig()
    val corpse = CorpseConfig()
    val message = MessageConfig()

    inner class GhostConfig {
        val enabled get() = config.getBoolean("ghost.enabled", true)
        val duration get() = config.getInt("ghost.duration", 10)
        val invisible get() = config.getBoolean("ghost.invisible", true)
        val allowChat get() = config.getBoolean("ghost.allow-chat", true)
    }

    inner class CorpseConfig {
        val enabled get() = config.getBoolean("grave.enabled", true)
        val expireSeconds get() = config.getInt("grave.expire-seconds", 300)
        val hologram get() = config.getBoolean("grave.hologram", true)
    }

    inner class MessageConfig {
        val enabled get() = config.getBoolean("message.enabled", true)
        val broadcast get() = config.getBoolean("message.broadcast", true)
    }
}