package org.firestorm.deathproRemake.manager

import org.bukkit.Location
import org.firestorm.deathproRemake.dto.GhostLocationData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostRespawnPending {
    private val pending = ConcurrentHashMap<UUID, GhostLocationData>()
    private val resolving: ConcurrentHashMap.KeySetView<UUID, Boolean> = ConcurrentHashMap.newKeySet<UUID>()

    fun add(uuid: UUID, deathLocation: Location, respawnLocation: Location) { pending[uuid] = GhostLocationData(deathLocation, respawnLocation) }
    fun remove(uuid: UUID) { pending.remove(uuid) }
    fun get(uuid: UUID) = pending[uuid]
    fun isPending(uuid: UUID) = pending.containsKey(uuid)

    fun startResolving(uuid: UUID) = resolving.add(uuid)
    fun isResolving(uuid: UUID) = resolving.contains(uuid)
    fun stopResolving(uuid: UUID) = resolving.remove(uuid)
}