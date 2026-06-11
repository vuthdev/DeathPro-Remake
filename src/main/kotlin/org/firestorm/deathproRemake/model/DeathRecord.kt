package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.DeathCause
import java.util.UUID

data class DeathRecord(
    val playerUuid: UUID,
    val playerName: String,
    val location: Location,
    val cause: DeathCause,
    val killerName: String?,
    val timestamp: Long = System.currentTimeMillis(),
)
