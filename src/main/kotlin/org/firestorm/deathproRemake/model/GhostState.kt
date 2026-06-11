package org.firestorm.deathproRemake.model

import org.bukkit.Location
import java.util.UUID

data class GhostState(
    val playerUuid: UUID,
    val playerName: String,
    val deathLocation: Location,
    val respawnLocation: Location,
    val expiresAt: Long,
    val taskId: Int = -1,
) {
    val remainingSeconds: Long
        get() = ((expiresAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAt
}
