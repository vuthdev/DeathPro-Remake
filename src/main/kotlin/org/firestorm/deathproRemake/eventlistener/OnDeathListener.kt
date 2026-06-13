package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.scheduler.BukkitScheduler
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

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            // force respawn
            player.spigot().respawn()
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                p.ghostService.enterGhostMode(player, deathLocation)
            }, 1L)
        }, 1L)
    }
}