package org.firestorm.deathproRemake.eventlistener

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost

class OnGhostState(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler(priority = high)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (player.isGhost()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (player.isGhost()) {
            event.isCancelled = true
        }
    }
}