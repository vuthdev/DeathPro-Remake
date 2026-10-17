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
    val expiresAt: Long? = null
) {

    val currentRemainingSeconds: Long
        get() {
            val deadline = expiresAt ?: return remainingSeconds
            val millis = deadline - System.currentTimeMillis()

            return if (millis <= 0L) 0L else (millis + 999L) / 1000L
        }

    val isExpired: Boolean
        get() = if (expiresAt != null) {
            System.currentTimeMillis() >= expiresAt
        } else {
            remainingSeconds <= 0L
        }
}