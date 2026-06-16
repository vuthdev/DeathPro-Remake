package org.firestorm.deathproRemake.storage.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID

object CorpseTable : Table("corpses") {
    val corpseId = integer("corpse_id")
    val playerUuid = javaUUID("player_uuid")
    val playerName = varchar("player_name", 16)
    val world = varchar("world", 100)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val skinTexture = varchar("skin_texture", 100)
    val skinSignature = varchar("skin_signature", 100).nullable()
    val expiredAt = long("expired_at")
    val spawnedAt = long("spawnedAt")

    // equipment store as string
    val equipmentJson = text("equipment_json").nullable()

    override val primaryKey = PrimaryKey(corpseId)
}