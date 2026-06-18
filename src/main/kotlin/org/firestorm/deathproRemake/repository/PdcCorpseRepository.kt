package org.firestorm.deathproRemake.repository

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.firestorm.deathproRemake.common.constants.CorpseKeys

object PdcCorpseRepository {
    fun save(player: Player, remainingSeconds: Long) {
        val pdc = player.persistentDataContainer
        pdc.set(CorpseKeys.REMAINING_SECONDS, PersistentDataType.LONG, remainingSeconds)
    }

    fun load(player: Player): Long {
        val pdc = player.persistentDataContainer
        return pdc.get(CorpseKeys.REMAINING_SECONDS, PersistentDataType.LONG) ?: 10
    }

    fun clear(player: Player) {
        val pdc = player.persistentDataContainer
        pdc.remove(CorpseKeys.REMAINING_SECONDS)
    }
}