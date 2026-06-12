package org.firestorm.deathproRemake.repository

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.common.constants.GhostKeys
import org.firestorm.deathproRemake.model.GhostState

class GhostRepository(private val plugin: DeathproRemake) {

    fun save(player: Player, ghostState: GhostState) {
        val pdc = player.persistentDataContainer
        pdc.set(GhostKeys.DEATH_WORLD, PersistentDataType.STRING, ghostState.deathLocation.world.name)
        pdc.set(GhostKeys.DEATH_X, PersistentDataType.DOUBLE, ghostState.deathLocation.x)
        pdc.set(GhostKeys.DEATH_Y, PersistentDataType.DOUBLE, ghostState.deathLocation.y)
        pdc.set(GhostKeys.DEATH_Z, PersistentDataType.DOUBLE, ghostState.deathLocation.z)
        pdc.set(GhostKeys.RESPAWN_WORLD, PersistentDataType.STRING, ghostState.respawnLocation.world.name)
        pdc.set(GhostKeys.RESPAWN_X, PersistentDataType.DOUBLE, ghostState.respawnLocation.x)
        pdc.set(GhostKeys.RESPAWN_Y, PersistentDataType.DOUBLE, ghostState.respawnLocation.y)
        pdc.set(GhostKeys.RESPAWN_Z, PersistentDataType.DOUBLE, ghostState.respawnLocation.z)
    }

    fun load(player: Player): GhostState {
        val pdc = player.persistentDataContainer
        val expireAt = pdc.get(GhostKeys.EXPIRE_AT, PersistentDataType.LONG)

        val deathWorldName = pdc.get(GhostKeys.DEATH_WORLD, PersistentDataType.STRING)
        val deathWorld = Bukkit.getWorld(deathWorldName ?: "world")

        val respawnWorldName = pdc.get(GhostKeys.RESPAWN_WORLD, PersistentDataType.STRING)
        val respawnWorld = Bukkit.getWorld(respawnWorldName ?: "world")

        return GhostState(
            playerUuid = player.uniqueId,
            playerName = player.name,
            deathLocation = Location(
                deathWorld,
                pdc.get(GhostKeys.DEATH_X, PersistentDataType.DOUBLE) ?: 0.0,
                pdc.get(GhostKeys.DEATH_Y, PersistentDataType.DOUBLE) ?: 0.0,
                pdc.get(GhostKeys.DEATH_Z, PersistentDataType.DOUBLE) ?: 0.0
            ),
            respawnLocation = Location(
                respawnWorld,
                pdc.get(GhostKeys.RESPAWN_X, PersistentDataType.DOUBLE) ?: 0.0,
                pdc.get(GhostKeys.RESPAWN_Y, PersistentDataType.DOUBLE) ?: 0.0,
                pdc.get(GhostKeys.RESPAWN_Z, PersistentDataType.DOUBLE) ?: 0.0
            ),
            expiresAt = expireAt ?: 5
        )
    }

    fun clear(player: Player) {
        val pdc = player.persistentDataContainer
        pdc.remove(GhostKeys.DEATH_WORLD)
        pdc.remove(GhostKeys.DEATH_X)
        pdc.remove(GhostKeys.DEATH_Y)
        pdc.remove(GhostKeys.DEATH_Z)
        pdc.remove(GhostKeys.RESPAWN_WORLD)
        pdc.remove(GhostKeys.RESPAWN_X)
        pdc.remove(GhostKeys.RESPAWN_Y)
        pdc.remove(GhostKeys.RESPAWN_Z)
        pdc.remove(GhostKeys.EXPIRE_AT)
    }

    fun exists(player: Player): Boolean {
        return player.persistentDataContainer.has(GhostKeys.EXPIRE_AT, PersistentDataType.LONG)
    }
}