package org.firestorm.deathproRemake

import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.handler.CommandRegistry
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.constants.GhostKeys
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.config.DeathProConfig
import org.firestorm.deathproRemake.eventlistener.DeathEventListener
import org.firestorm.deathproRemake.eventlistener.OnJoinListener
import org.firestorm.deathproRemake.eventlistener.OnQuitListener
import org.firestorm.deathproRemake.repository.GhostRepository
import org.firestorm.deathproRemake.service.GhostService

class DeathproRemake : JavaPlugin() {
    companion object {
        lateinit var instance: DeathproRemake
            private set
    }

    lateinit var deathProConfig: DeathProConfig
        private set
    lateinit var ghostService: GhostService
        private set
    lateinit var ghostRepository: GhostRepository
        private set

    override fun onEnable() {
        instance = this
        loadConfig()

        CommandRegistry.scan(this, DeathproRemake())
        GhostKeys.init(this)
        registerListener()
        registerRepository()
        registerService()

        clogger.info("${BaseConstants.PREFIX} &aplugin started".color())
    }

    override fun onDisable() {
        clogger.info("${BaseConstants.PREFIX} &cplugin stopped".color())
    }

    fun loadConfig() {
        saveDefaultConfig()
        deathProConfig = DeathProConfig(this)
    }

    fun registerListener() {
        DeathEventListener(this).register()
        OnJoinListener(this).register()
        OnQuitListener(this).register()
    }

    fun registerService() {
        ghostService = GhostService(this, ghostRepository)
    }

    fun registerRepository() {
        ghostRepository = GhostRepository(this)
    }
}
