package org.firestorm.deathproRemake.model

import org.bukkit.Location
import java.util.UUID

data class GhostState(
    var playerUuid: UUID,
    var playerName: String,
    var deathLocation: Location,
    var respawnLocation: Location,
    var remainingSeconds: Long,
    var taskId: Int = -1,
    var expiresAt: Long? = null,
) {
    val currentRemainingSeconds: Long
        get() = remainingSeconds

    val isExpired: Boolean
        get() = remainingSeconds <= 0L
}
