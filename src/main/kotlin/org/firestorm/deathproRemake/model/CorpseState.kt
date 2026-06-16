package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.utils.IDGeneratorUtil
import java.util.UUID
import kotlin.uuid.Uuid

data class CorpseState(
    var corpseId: Int = IDGeneratorUtil.generateId(),
    var playerName: String,
    var playerUuid: UUID,
    var location: Location,
    var skin: NpcSkinData,
    var expiredAt: Long,
    var spawnedAt: Long = System.currentTimeMillis(),
    var equipment: PlayerEquipment,
    var taskId: Int = -1,
) {
    val remainingSeconds: Long
        get() = ((expiredAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiredAt
}