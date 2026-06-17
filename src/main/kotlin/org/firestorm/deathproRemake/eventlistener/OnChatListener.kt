package org.firestorm.deathproRemake.eventlistener

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost

class OnChatListener(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        val player = e.player
        if (!player.isGhost() && !config.ghost.allowChat) return

        e.isCancelled = true
        player.sendMessage(messageConfig.ghostCantInteract)
    }
}