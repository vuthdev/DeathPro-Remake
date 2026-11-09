package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost
import org.firestorm.deathproRemake.manager.CorpseTaskManager
import org.firestorm.deathproRemake.manager.CountdownStore
import org.firestorm.deathproRemake.manager.GhostTaskManager
import org.firestorm.deathproRemake.repository.CorpseRepository
import org.firestorm.deathproRemake.repository.GhostRepository
import org.firestorm.deathproRemake.storage.database.DbExecutor

class OnQuitListener(
    private val p: DeathproRemake,
): BaseListener(p) {

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        val uuid = player.uniqueId
        val live = CountdownStore.remove(uuid) ?: return

        if (player.isGhost()) {
            GhostTaskManager.cancel(player.uniqueId)
            DbExecutor.submit { GhostRepository.updateRemainingSeconds(uuid, live) }

            if (!plugin.isEnabled) {
                saveCorpseData(player)
            } else {
                scheduler.runTaskAsynchronously(plugin, Runnable {
                    saveCorpseData(player)
                })
            }
        }
    }

    private fun saveCorpseData(player: Player) {
        val uuid = player.uniqueId
        val corpses = CorpseRepository.loadActive()   // move this off-thread too if it's large
            .filter { it.playerUuid == uuid }

        corpses.forEach { CorpseTaskManager.cancel(it.corpseId) }

        DbExecutor.submit {
            CorpseRepository.updateRemainingSecondsBatch(
                corpses.map { Triple(it.corpseId, it.playerUuid, it.remainingSeconds) }
            )
        }
    }
}