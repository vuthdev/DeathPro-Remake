package org.firestorm.deathproRemake.repository

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.storage.database.table.CorpseTable
import org.firestorm.deathproRemake.storage.database.table.CorpseTable.remainingSeconds
import org.firestorm.deathproRemake.storage.database.table.GhostTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert
import java.util.UUID

object GhostRepository {

    fun insert(state: GhostState) = transaction {
        GhostTable.upsert {
            it[playerUuid] = state.playerUuid
            it[deathWorld] = state.deathLocation.world.name
            it[deathX] = state.deathLocation.x
            it[deathY] = state.deathLocation.y
            it[deathZ] = state.deathLocation.z
            it[respawnWorld] = state.respawnLocation.world.name
            it[respawnX] = state.respawnLocation.x
            it[respawnY] = state.respawnLocation.y
            it[respawnZ] = state.respawnLocation.z
            it[remainingSeconds] = state.remainingSeconds
            it[expiresAt] = state.expiresAt
        }

        GhostCache.add(state.playerUuid)
    }

    fun get(player: Player): GhostState? {
        val data = transaction {
            GhostTable
                .select(GhostTable.columns)
                .where { GhostTable.playerUuid eq player.uniqueId }
                .firstOrNull()
        } ?: return null

        val deathWorldName = data.getOrNull(GhostTable.deathWorld) ?: "world"
        val deathX = data[GhostTable.deathX]
        val deathY = data[GhostTable.deathY]
        val deathZ = data[GhostTable.deathZ]

        val respawnWorldName = data.getOrNull(GhostTable.respawnWorld) ?: "world"
        val respawnX = data[GhostTable.respawnX]
        val respawnY = data[GhostTable.respawnY]
        val respawnZ = data[GhostTable.respawnZ]

        val deathWorld = Bukkit.getWorld(deathWorldName)
        val respawnWorld = Bukkit.getWorld(respawnWorldName)

        return GhostState(
            playerUuid = player.uniqueId,
            playerName = player.name,
            deathLocation = Location(
                deathWorld,
                deathX,
                deathY,
                deathZ
            ),
            respawnLocation = Location(
                respawnWorld,
                respawnX,
                respawnY,
                respawnZ
            ),
            remainingSeconds = data[GhostTable.remainingSeconds],
            expiresAt = data[GhostTable.expiresAt]
        )
    }

    fun delete(playerUuid: UUID) = transaction {
        GhostTable.deleteWhere {
            GhostTable.playerUuid eq playerUuid
        }

        GhostCache.remove(playerUuid)
    }

    fun updateRemainingSeconds(playerUuid: UUID, seconds: Long) = transaction {
        GhostTable.update( where = { (GhostTable.playerUuid eq playerUuid) } ) {
            it[remainingSeconds] = seconds
        }
    }

    fun exists(playerUuid: UUID): Boolean = transaction {
        !GhostTable
            .select(GhostTable.playerUuid)
            .where { GhostTable.playerUuid eq playerUuid }
            .empty()
    }

}