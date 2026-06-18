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
    var remainingSeconds: Long,
    var spawnedAt: Long = System.currentTimeMillis(),
    var equipment: PlayerEquipment,
    var taskId: Int = -1,
) {

    val isExpired: Boolean
        get() = remainingSeconds <= 0
}