package org.firestorm.deathproRemake.manager

import org.firestorm.deathproRemake.model.GhostState
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostManager {
    private val ghosts = ConcurrentHashMap<UUID, GhostState>()

    fun add(state: GhostState) { ghosts[state.playerUuid] = state }
    fun remove(uuid: UUID) = ghosts.remove(uuid)
    fun get(uuid: UUID) = ghosts[uuid]
    fun isGhost(uuid: UUID) = ghosts.containsKey(uuid)
    fun all() = ghosts.values.toList()

    fun update(uuid: UUID, block: (GhostState) -> GhostState) {
        ghosts.computeIfPresent(uuid) { _, state -> block(state)}
    }
}