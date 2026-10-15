package org.firestorm.deathproRemake.repository

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.firestorm.deathproRemake.storage.database.table.PendingDeathTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import java.util.UUID

object PendingDeathInvRepository {

    fun insert(playerUUID: UUID, contents: Array<ItemStack?>) {
        val bytes = ItemStack.serializeItemsAsBytes(contents)
        transaction {
            PendingDeathTable.upsert {
                it[PendingDeathTable.playerUuid] = playerUUID
                it[PendingDeathTable.inventories] = bytes
                it[createdAt] = System.currentTimeMillis()
            }
        }
    }

    fun loadPlayerInvAsByteArray(playerUUID: UUID): ByteArray? {
        return transaction {
            PendingDeathTable
                .selectAll()
                .where { PendingDeathTable.playerUuid eq playerUUID }
                .firstOrNull()
                ?.get(PendingDeathTable.inventories)
        }
    }

    fun removePlayerInv(playerUUID: UUID) = transaction {
        PendingDeathTable.deleteWhere { PendingDeathTable.playerUuid eq playerUUID }
    }
}