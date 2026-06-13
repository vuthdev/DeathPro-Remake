package org.firestorm.deathproRemake.eventlistener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import org.bukkit.event.EventHandler
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.manager.GhostPostRespawnPending

class OnPostRespawnListener(private val p: DeathproRemake) : BaseListener(p) {

    @EventHandler
    fun onPostRespawn(e: PlayerPostRespawnEvent) {
        val player = e.player
        val data = GhostPostRespawnPending.get(player.uniqueId) ?: return
        GhostPostRespawnPending.remove(player.uniqueId)

        p.ghostService.enterGhostMode(player, data.deadLocation, data.respawnLocation)
    }
}