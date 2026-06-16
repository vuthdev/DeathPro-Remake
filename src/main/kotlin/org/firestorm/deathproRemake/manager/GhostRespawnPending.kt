package org.firestorm.deathproRemake.manager

import org.bukkit.Location
import org.firestorm.deathproRemake.model.GhostLocationData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostRespawnPending {
    private val pending = ConcurrentHashMap<UUID, Location>()

    fun add(uuid: UUID, deathLocation: Location) { pending[uuid] = deathLocation }
    fun remove(uuid: UUID) { pending.remove(uuid) }
    fun get(uuid: UUID) = pending[uuid]
    fun isPending(uuid: UUID) = pending.containsKey(uuid)
}