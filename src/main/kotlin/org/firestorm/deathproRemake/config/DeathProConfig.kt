package org.firestorm.deathproRemake.config

import org.bukkit.plugin.java.JavaPlugin

class DeathProConfig(plugin: JavaPlugin) {
    private val config = plugin.config

    val ghost = GhostConfig()
    val grave = GraveConfig()
    val penalty = PenaltyConfig()
    val message = MessageConfig()

    inner class GhostConfig {
        val enabled get() = config.getBoolean("ghost.enabled", true)
        val duration get() = config.getInt("ghost.duration", 10)
        val invisible get() = config.getBoolean("ghost.invisible", true)
        val allowChat get() = config.getBoolean("ghost.allow-chat", true)
    }

    inner class GraveConfig {
        val enabled get() = config.getBoolean("grave.enabled", true)
        val expireSeconds get() = config.getInt("grave.expire-seconds", 300)
        val hologram get() = config.getBoolean("grave.hologram", true)
    }

    inner class PenaltyConfig {
        val enabled get() = config.getBoolean("penalty.enabled", false)
        val xpLossPercent get() = config.getDouble("penalty.xp-loss-percent", 0.0)
    }

    inner class MessageConfig {
        val enabled get() = config.getBoolean("message.enabled", true)
        val broadcast get() = config.getBoolean("message.broadcast", true)
    }
}