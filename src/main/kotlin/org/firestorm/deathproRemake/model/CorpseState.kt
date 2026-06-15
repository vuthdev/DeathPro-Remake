package org.firestorm.deathproRemake.model

import org.bukkit.Location
import java.util.UUID

data class CorpseState(
    var entityId: Int,
    var playerName: String,
    var location: Location,
    var skin: NpcSkinData,
    var expiredAt: Long,
    var taskId: Int = -1,
) {
    val remainingSeconds: Long
        get() = ((expiredAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiredAt
}