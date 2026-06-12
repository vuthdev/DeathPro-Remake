package org.firestorm.deathproRemake.common.extension

import org.bukkit.entity.Player
import org.firestorm.deathproRemake.manager.GhostManager

fun Player.isGhost(): Boolean {
    return GhostManager.isGhost(this.uniqueId)
}