package org.firestorm.deathproRemake.storage.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID

object PendingDeathTable: Table("pending_death_inventories") {
    val playerUuid = javaUUID("player_uuid")
    val inventories = binary("inventories")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(playerUuid)
}