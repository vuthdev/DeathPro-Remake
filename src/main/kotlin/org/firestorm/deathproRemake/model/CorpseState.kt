package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.CorpseStateEnum
import java.util.UUID

data class CorpseState(
    var entityId: Int,
    var playerUuid: UUID,
    var playerName: String,
    var location: Location,
    var skinTexture: String,
    var skinSignature: String,
    var expireAt: Long,
)