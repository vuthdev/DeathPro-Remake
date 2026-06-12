package org.firestorm.deathproRemake

import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.handler.CommandRegistry
import org.firestorm.deathproRemake.config.DeathProConfig
import org.firestorm.deathproRemake.eventlistener.DeathEventListener
import org.firestorm.deathproRemake.service.GhostService
import org.firestorm.deathproRemake.storage.YamlDataStorage

class DeathproRemake : JavaPlugin() {
    lateinit var deathProConfig: DeathProConfig
        private set
    lateinit var ghostService: GhostService
        private set

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        loadConfig()

        CommandRegistry.scan(this, DeathproRemake())
        registerListener()

        val storage = YamlDataStorage(this)
        ghostService = GhostService(this)

    }

    override fun onDisable() {
        if (PacketEvents.getAPI() != null) {
            PacketEvents.getAPI().terminate()
        }
    }

    fun loadConfig() {
        saveDefaultConfig()
        deathProConfig = DeathProConfig(this)
    }

    fun registerListener() {
        DeathEventListener(this).register()
    }
}
