package org.firestorm.deathproRemake.common.extension

import org.bukkit.entity.Player
import org.firestorm.deathproRemake.repository.GhostCache

fun Player.isGhost(): Boolean {
    return GhostCache.contains(this.uniqueId)
}