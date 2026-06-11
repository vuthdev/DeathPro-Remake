package org.firestorm.deathproRemake

import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.handler.CommandRegistry
import org.firestorm.deathproRemake.config.DeathProConfig

class DeathproRemake : JavaPlugin() {
    lateinit var deathProConfig: DeathProConfig
        private set;



    override fun onEnable() {
        CommandRegistry.scan(this, DeathproRemake())
    }

    override fun onDisable() {

    }
}
