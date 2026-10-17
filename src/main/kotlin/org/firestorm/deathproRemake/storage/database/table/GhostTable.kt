package org.firestorm.deathproRemake.storage.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID

object GhostTable: Table("ghosts") {
    val playerUuid = javaUUID("player_uuid")
    val deathWorld = text("death_world")
    val deathX = double("death_x")
    val deathY = double("death_y")
    val deathZ = double("death_z")
    val respawnWorld = text("respawn_word")
    val respawnX = double("respawn_x")
    val respawnY = double("respawn_y")
    val respawnZ = double("respawn_z")
    val remainingSeconds = long("remaining_seconds")
    val expiresAt = long("expires_at").nullable()
}