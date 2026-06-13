package org.firestorm.deathproRemake.common.extension

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.firestorm.deathproRemake.common.constants.GhostKeys

fun Player.isGhost(): Boolean {
    return this.persistentDataContainer.has(GhostKeys.EXPIRE_AT, PersistentDataType.LONG)
}