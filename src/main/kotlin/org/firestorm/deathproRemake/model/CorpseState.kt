package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.CorpseStateEnum
import java.util.UUID

data class CorpseState(
    val entityId: Int,
    val playerUuid: UUID,
    val playerName: String,
    val location: Location,
    val skinTexture: String,
    val skinSignature: String,
    val expireAt: Long,
)