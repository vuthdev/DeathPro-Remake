package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerRespawnEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.manager.GhostPostRespawnPending
import org.firestorm.deathproRemake.manager.GhostRespawnPending

class OnRespawnListener(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(e: PlayerRespawnEvent) {
        val player = e.player

        if (GhostRespawnPending.isResolving(player.uniqueId)) return

        val data = GhostRespawnPending.get(player.uniqueId) ?: return
        GhostRespawnPending.remove(player.uniqueId)

        // override respawn location
        e.respawnLocation = data.deadLocation

        GhostPostRespawnPending.add(player.uniqueId, data)
    }
}