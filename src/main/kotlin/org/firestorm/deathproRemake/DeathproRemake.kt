package org.firestorm.deathproRemake

import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.handler.CommandRegistry
import org.firestorm.deathproRemake.config.DeathProConfig
import org.firestorm.deathproRemake.service.GhostService
import org.firestorm.deathproRemake.service.GraveService
import org.firestorm.deathproRemake.storage.YamlDataStorage

class DeathproRemake : JavaPlugin() {
    lateinit var deathProConfig: DeathProConfig
        private set;
    lateinit var ghostService: GhostService

    override fun onEnable() {
        saveDefaultConfig()
        deathProConfig = DeathProConfig(this)
        CommandRegistry.scan(this, DeathproRemake())

        val storage = YamlDataStorage(this)
        ghostService = GhostService(this)

    }

    override fun onDisable() {

    }
}
