package org.firestorm.deathproRemake.eventlistener

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.common.extension.isGhost

class OnPlayerCommandListener(private val p: DeathproRemake): BaseListener(p) {
    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        val player = e.player

        if (player.isGhost()) {
            player.sendMessage("${BaseConstants.PREFIX} &cyou cannot command while in ghost mode!".color())
            e.isCancelled = true
        }
    }
}