package org.firestorm.deathproRemake.manager

import com.github.retrooper.packetevents.protocol.player.UserProfile
import org.firestorm.deathproRemake.model.CorpseState
import java.util.UUID

object CorpseManager {
    val corpses = mutableMapOf<UUID, CorpseState>()

    fun add(uuid: UUID, corpse: CorpseState) { corpses[uuid] = corpse }
    fun update(uuid: UUID, corpse: CorpseState) { corpses[uuid] = corpse }
    fun get(uuid: UUID) = corpses[uuid]
    fun remove(uuid: UUID) = corpses.remove(uuid)
}