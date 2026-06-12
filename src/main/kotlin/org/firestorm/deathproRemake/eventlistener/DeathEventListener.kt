package org.firestorm.deathproRemake.eventlistener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost

class DeathEventListener(
    private val p: DeathproRemake,
): BaseListener(p) {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player: Player = e.player

        val deathLocation = player.location
        if (player.isGhost()) {
            p.ghostService.enterGhostMode(player, deathLocation)
        }
    }
}