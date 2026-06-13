package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.CorpseStateEnum
import java.util.UUID

data class GhostState(
    var playerUuid: UUID,
    var playerName: String,
    var deathLocation: Location,
    var respawnLocation: Location,
    var expiredAt: Long,
    var taskId: Int = -1,
) {
    val remainingSeconds: Long
        get() = ((expiredAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiredAt
}
