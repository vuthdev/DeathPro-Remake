package org.firestorm.deathproRemake.common.extension

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.firestorm.deathproRemake.common.constants.GhostKeys
import org.firestorm.deathproRemake.repository.GhostCache
import org.firestorm.deathproRemake.repository.GhostRepository

fun Player.isGhost(): Boolean {
    return GhostCache.contains(this.uniqueId)
}