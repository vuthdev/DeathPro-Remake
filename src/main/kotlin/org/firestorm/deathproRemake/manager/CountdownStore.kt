package org.firestorm.deathproRemake.manager

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object CountdownStore {
    private val remaining = ConcurrentHashMap<UUID, Long>()

    fun set(uuid: UUID, seconds: Long) { remaining[uuid] = seconds }
    fun get(uuid: UUID): Long? = remaining[uuid]
    fun remove(uuid: UUID) { remaining.remove(uuid) }
    fun snapshot(): Map<UUID, Long> = HashMap(remaining)
}