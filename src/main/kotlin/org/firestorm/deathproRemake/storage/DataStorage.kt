package org.firestorm.deathproRemake.storage

import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.model.GraveState
import java.util.UUID

interface DataStorage {
    fun saveGhostState(state: GhostState)
    fun removeGhostState(playerUuid: UUID)
    fun loadAllGhostStates(): List<GhostState>

    fun saveGraveState(state: GraveState)
    fun removeGraveState(graveUuid: UUID)
    fun loadAllGraveStates(): List<GraveState>
}