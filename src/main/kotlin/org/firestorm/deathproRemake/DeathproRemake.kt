package org.firestorm.deathproRemake

import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.handler.CommandRegistry
import org.firestorm.deathproRemake.commands.DeathProCommand
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.constants.GhostKeys
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.config.DeathProConfig
import org.firestorm.deathproRemake.eventlistener.OnDeathListener
import org.firestorm.deathproRemake.eventlistener.OnGhostStateListener
import org.firestorm.deathproRemake.eventlistener.OnJoinListener
import org.firestorm.deathproRemake.eventlistener.OnPlayerCommandListener
import org.firestorm.deathproRemake.eventlistener.OnPostRespawnListener
import org.firestorm.deathproRemake.eventlistener.OnQuitListener
import org.firestorm.deathproRemake.eventlistener.OnRespawnListener
import org.firestorm.deathproRemake.eventlistener.SpawnMessageSuppressor
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

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        registerPacketListener()
    }

    override fun onEnable() {
        instance = this

        PacketEvents.getAPI().init();

        loadConfig()

        CommandRegistry.scan(this, DeathProCommand())
        GhostKeys.init(this)

        registerListener()
        registerRepository()
        registerService()

        logger.info("${BaseConstants.PREFIX} &aplugin started")
    }

    override fun onDisable() {
        logger.info("${BaseConstants.PREFIX} &cplugin stopped")
        PacketEvents.getAPI().terminate()
    }

    fun loadConfig() {
        saveDefaultConfig()
        deathProConfig = DeathProConfig(this)
    }

    fun registerListener() {
        OnDeathListener(this).register()
        OnRespawnListener(this).register()
        OnPostRespawnListener(this).register()
        OnJoinListener(this).register()
        OnQuitListener(this).register()
        OnGhostStateListener(this).register()
        OnPlayerCommandListener(this).register()
    }

    fun registerPacketListener() {
        SpawnMessageSuppressor().register()
    }

    fun registerService() {
        ghostService = GhostService(this, ghostRepository)
    }

    fun registerRepository() {
        ghostRepository = GhostRepository(this)
    }
}
