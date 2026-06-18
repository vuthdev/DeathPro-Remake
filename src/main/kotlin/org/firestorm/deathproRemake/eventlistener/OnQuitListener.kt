package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost
import org.firestorm.deathproRemake.manager.CorpseTaskManager
import org.firestorm.deathproRemake.manager.GhostTaskManager
import org.firestorm.deathproRemake.repository.CorpseRepository
import org.firestorm.deathproRemake.repository.PdcCorpseRepository

class OnQuitListener(
    private val p: DeathproRemake,
): BaseListener(p) {

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player

        if (!player.isGhost()) return
        GhostTaskManager.cancel(player.uniqueId)
        val seconds = PdcCorpseRepository.load(player)
        scheduler.runTaskAsynchronously(plugin, Runnable {
            val playersCorpses = CorpseRepository.loadActive().filter { it.playerUuid == player.uniqueId }

            playersCorpses.forEach {
                CorpseRepository.updateRemainingSeconds(it.corpseId, player, seconds)
            }
        })
    }
}