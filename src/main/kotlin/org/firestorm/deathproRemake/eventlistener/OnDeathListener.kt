package org.firestorm.deathproRemake.eventlistener

import com.google.common.collect.ImmutableSet
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.manager.GhostRespawnPending

class OnDeathListener(
    private val p: DeathproRemake,
): BaseListener(p) {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player: Player = e.player

        e.deathMessage(null)

        val location = player.location
        val deathLocation = location.clone().apply {
            y = location.y + 1
        }

        GhostRespawnPending.startResolving(player.uniqueId)
        val respawnLocation = getRespawnLocation(player)
        GhostRespawnPending.stopResolving(player.uniqueId)

        GhostRespawnPending.add(player.uniqueId, deathLocation, respawnLocation)

        player.sendMessage("you dead ${player.name}.")

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            // force respawn
            player.spigot().respawn()
        }, 1L)
    }

    fun getRespawnLocation(player: Player): Location {
        val spawnLocation = player.respawnLocation
        val isBedSpawn = spawnLocation != null
        val defaultLoc = spawnLocation ?: player.world.spawnLocation
        val respawnFlag = ImmutableSet.Builder<PlayerRespawnEvent.RespawnFlag>().add(PlayerRespawnEvent.RespawnFlag.BED_SPAWN)

        val respawnEvent = PlayerRespawnEvent(
            player,
            defaultLoc,
            isBedSpawn,
            true,
            PlayerRespawnEvent.RespawnReason.PLUGIN,
            respawnFlag
        )
        Bukkit.getPluginManager().callEvent(respawnEvent)

        return respawnEvent.respawnLocation
    }
}