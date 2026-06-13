package org.firestorm.deathproRemake.manager

import org.firestorm.deathproRemake.model.CorpseState
import java.util.UUID

object CorpseManager {
    private val corpses = mutableMapOf<UUID, CorpseState>()

    fun add(state: CorpseState) { corpses[state.playerUuid] = state}
    fun remove(uuid: UUID) = corpses.remove(uuid)
    fun get(uuid: UUID) = corpses[uuid]
}