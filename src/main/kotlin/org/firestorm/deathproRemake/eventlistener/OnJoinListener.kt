package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost

class OnJoinListener(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        if (!player.isGhost()) return

        scheduler.runTaskLater(plugin, Runnable {
            if (!player.isOnline) return@Runnable

            p.corpseService.restoreCorpse(player)
            p.ghostService.restoreGhostMode(player)
        }, 10L)
    }
}