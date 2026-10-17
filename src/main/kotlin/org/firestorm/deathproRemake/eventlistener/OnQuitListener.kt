package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost
import org.firestorm.deathproRemake.manager.CorpseTaskManager
import org.firestorm.deathproRemake.manager.GhostTaskManager
import org.firestorm.deathproRemake.repository.CorpseRepository
import org.firestorm.deathproRemake.repository.GhostRepository

class OnQuitListener(
    private val p: DeathproRemake,
): BaseListener(p) {

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player

        if (player.isGhost()) {
            GhostTaskManager.cancel(player.uniqueId)

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
        val corpses = CorpseRepository.loadActive()
            .filter { it.playerUuid == player.uniqueId }

        corpses.forEach { corpse ->
            // cancel corpse on quit
            CorpseTaskManager.cancel(corpse.corpseId)

            CorpseRepository.updateRemainingSeconds(
                corpse.corpseId,
                corpse.playerUuid,
                corpse.remainingSeconds
            )
        }
    }
}