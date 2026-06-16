package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerRespawnEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.manager.GhostPostRespawnPending
import org.firestorm.deathproRemake.manager.GhostRespawnPending
import org.firestorm.deathproRemake.model.GhostLocationData

class OnRespawnListener(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRespawn(e: PlayerRespawnEvent) {
        val player = e.player

        val respawnLocation = e.respawnLocation.clone()

        val deathLocation = GhostRespawnPending.get(player.uniqueId) ?: return
        GhostRespawnPending.remove(player.uniqueId)

        // override respawn location
        e.respawnLocation = deathLocation

        val ghostLocation = GhostLocationData(
            deathLocation,
            respawnLocation
        )

        GhostPostRespawnPending.add(player.uniqueId, ghostLocation)
    }
}