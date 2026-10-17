package org.firestorm.deathproRemake.repository

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostCache {
    private val ghosts = ConcurrentHashMap.newKeySet<UUID>()

    fun add(uuid: UUID) {
        ghosts.add(uuid)
    }

    fun remove(uuid: UUID) {
        ghosts.remove(uuid)
    }

    fun contains(uuid: UUID): Boolean {
        return uuid in ghosts
    }
}