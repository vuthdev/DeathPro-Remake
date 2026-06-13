package org.firestorm.deathproRemake.manager

import org.firestorm.deathproRemake.dto.GhostLocationData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostPostRespawnPending {
    private val pending = ConcurrentHashMap<UUID, GhostLocationData>()

    fun add(uuid: UUID, data: GhostLocationData) {
        pending[uuid] = data
    }
    fun remove(uuid: UUID) = pending.remove(uuid)
    fun get(uuid: UUID) = pending[uuid]
}