package org.firestorm.deathproRemake.model

import org.bukkit.Location
import org.firestorm.deathproRemake.common.enums.DeathCause
import java.time.Instant
import java.util.UUID

data class DeathRecord(
    var playerUuid: UUID,
    var playerName: String,
    var location: Location,
    var cause: DeathCause,
    var killerName: String?,
    var timestamp: Instant = Instant.now(),
)
