package org.firestorm.deathproRemake.repository

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.common.utils.EquipmentSerializer
import org.firestorm.deathproRemake.model.CorpseState
import org.firestorm.deathproRemake.model.NpcSkinData
import org.firestorm.deathproRemake.storage.database.table.CorpseTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert
import java.util.UUID

object CorpseRepository {

    fun insert(state: CorpseState) = transaction {
        CorpseTable.upsert {
            it[corpseId] = state.corpseId
            it[playerUuid] = state.playerUuid
            it[playerName] = state.playerName
            it[world] = state.location.world?.name ?: "world"
            it[x] = state.location.x
            it[y] = state.location.y
            it[z] = state.location.z
            it[skinTexture] = state.skin.skinTexture
            it[skinSignature] = state.skin.skinSignature
            it[remainingSeconds] = state.remainingSeconds
            it[spawnedAt] = state.spawnedAt

            // convert to json
            it[equipmentJson] = EquipmentSerializer.toJson(state.equipment)
        }
    }

    fun updateRemainingSeconds(corpseId: Int, player: Player, seconds: Long) = transaction {
        CorpseTable.update( { (CorpseTable.corpseId eq corpseId) and (CorpseTable.playerUuid eq player.uniqueId) } ) {
            it[remainingSeconds] = seconds
        }
    }

    fun delete(corpseId: Int) = transaction {
        CorpseTable.deleteWhere {
            CorpseTable.corpseId eq corpseId
        }
    }

    fun deleteExpired() = transaction {
        CorpseTable.deleteWhere {
            remainingSeconds lessEq 0
        }
    }

    fun loadActive(): List<CorpseState> = transaction {
        CorpseTable.selectAll()
            .where { CorpseTable.remainingSeconds greater 0 }
            .mapNotNull { runCatching { it.toCorpseState() }.getOrNull() }
    }

    /*
     ==============================
     ====== PRIVATE FUNCTION ======
     ==============================
     */

    private fun ResultRow.toCorpseState(): CorpseState? {
        val world = Bukkit.getWorld(this[CorpseTable.world]) ?: return null
        return CorpseState(
            corpseId = this[CorpseTable.corpseId],
            playerName = this[CorpseTable.playerName],
            playerUuid = this[CorpseTable.playerUuid],
            location = Location(
                world,
                this[CorpseTable.x],
                this[CorpseTable.y],
                this[CorpseTable.z]
            ),
            skin = NpcSkinData(
                skinTexture = this[CorpseTable.skinTexture],
                skinSignature = this[CorpseTable.skinSignature]
            ),
            remainingSeconds = this[CorpseTable.remainingSeconds],
            spawnedAt = this[CorpseTable.spawnedAt],
            equipment = EquipmentSerializer.fromJson(this[CorpseTable.equipmentJson])
        )
    }
}