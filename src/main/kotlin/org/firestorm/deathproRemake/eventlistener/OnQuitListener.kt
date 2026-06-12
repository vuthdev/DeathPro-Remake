package org.firestorm.deathproRemake.eventlistener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost
import org.firestorm.deathproRemake.manager.GhostManager

class OnQuitListener(
    private val p: DeathproRemake,
): BaseListener(p) {

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player

        if (player.isGhost()) {
            val state = GhostManager.get(player.uniqueId) ?: return
            Bukkit.getScheduler().cancelTask(state.taskId)
            GhostManager.remove(player.uniqueId)
        }
    }
}