package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.GraveStateEnum
import java.util.UUID

data class GraveState(
    val uuid: UUID = UUID.randomUUID(),
    val playerUuid: UUID,
    val playerName: String,
    val location: Location,
    val expiresAt: Long,
    val state: GraveStateEnum = GraveStateEnum.ACTIVE,
    val taskId: Int = -1,
) {
    val remainingSeconds: Long
        get() = ((expiresAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAt
}
