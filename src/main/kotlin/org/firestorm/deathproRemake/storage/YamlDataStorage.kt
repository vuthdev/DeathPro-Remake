package org.firestorm.deathproRemake.storage

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.common.enums.GraveStateEnum
import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.model.GraveState
import java.io.File
import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock

class YamlDataStorage(private val plugin: DeathproRemake) : DataStorage {

    private val file = File(plugin.dataFolder, "data.yml")
    private var yaml = YamlConfiguration.loadConfiguration(file)
    private val lock = ReentrantReadWriteLock()

    // Ghost

    override fun saveGhostState(state: GhostState) {
        lock.writeLock().lock()
        try {
            val path = "ghosts.${state.playerUuid}"
            yaml.set("$path.playerName", state.playerName)
            yaml.set("$path.expiresAt", state.expiresAt)
            yaml.set("$path.deathWorld", state.deathLocation.world?.name)
            yaml.set("$path.deathX", state.deathLocation.x)
            yaml.set("$path.deathY", state.deathLocation.y)
            yaml.set("$path.deathZ", state.deathLocation.z)
            yaml.set("$path.respawnWorld", state.respawnLocation.world?.name)
            yaml.set("$path.respawnX", state.respawnLocation.x)
            yaml.set("$path.respawnY", state.respawnLocation.y)
            yaml.set("$path.respawnZ", state.respawnLocation.z)
        } finally {
            lock.writeLock().unlock()
        }
        save()
    }

    override fun removeGhostState(playerUuid: UUID) {
        lock.writeLock().lock()
        try {
            yaml.set("ghosts.$playerUuid", null)
        } finally {
            lock.writeLock().unlock()
        }
        save()
    }

    override fun loadAllGhostStates(): List<GhostState> {
        val section = yaml.getConfigurationSection("ghosts") ?: return emptyList()
        return section.getKeys(false).mapNotNull { key ->
            val path = "ghosts.$key"
            val deathWorld  = Bukkit.getWorld(yaml.getString("$path.deathWorld") ?: return@mapNotNull null) ?: return@mapNotNull null
            val respawnWorld = Bukkit.getWorld(yaml.getString("$path.respawnWorld") ?: return@mapNotNull null) ?: return@mapNotNull null
            GhostState(
                playerUuid = UUID.fromString(key),
                playerName = yaml.getString("$path.playerName") ?: key,
                expiresAt = yaml.getLong("$path.expiresAt"),
                deathLocation = Location(deathWorld, yaml.getDouble("$path.deathX"),   yaml.getDouble("$path.deathY"),   yaml.getDouble("$path.deathZ")),
                respawnLocation = Location(respawnWorld, yaml.getDouble("$path.respawnX"), yaml.getDouble("$path.respawnY"), yaml.getDouble("$path.respawnZ")),
            )
        }
    }

    // Grave

    override fun saveGraveState(state: GraveState) {
        lock.writeLock().lock()
        try {
            val path = "graves.${state.uuid}"
            yaml.set("$path.playerUuid", state.playerUuid.toString())
            yaml.set("$path.playerName", state.playerName)
            yaml.set("$path.expiresAt", state.expiresAt)
            yaml.set("$path.state", state.state.name)
            yaml.set("$path.world", state.location.world?.name)
            yaml.set("$path.x", state.location.x)
            yaml.set("$path.y", state.location.y)
            yaml.set("$path.z", state.location.z)
        } finally {
            lock.writeLock().unlock()
        }
        save()
    }

    override fun removeGraveState(graveUuid: UUID) {
        yaml.set("graves.$graveUuid", null)
        save()
    }

    override fun loadAllGraveStates(): List<GraveState> {
        val section = yaml.getConfigurationSection("graves") ?: return emptyList()
        return section.getKeys(false).mapNotNull { key ->
            val path = "graves.$key"
            val world = Bukkit.getWorld(yaml.getString("$path.world") ?: return@mapNotNull null) ?: return@mapNotNull null
            GraveState(
                uuid = UUID.fromString(key),
                playerUuid = UUID.fromString(yaml.getString("$path.playerUuid") ?: return@mapNotNull null),
                playerName = yaml.getString("$path.playerName") ?: "",
                expiresAt = yaml.getLong("$path.expiresAt"),
                state = GraveStateEnum.valueOf(yaml.getString("$path.state") ?: "ACTIVE"),
                location = Location(world, yaml.getDouble("$path.x"), yaml.getDouble("$path.y"), yaml.getDouble("$path.z")),
            )
        }
    }

    // Internal

    private fun save() {
        yaml.save(file)
    }

    fun reload() {
        yaml = YamlConfiguration.loadConfiguration(file)
    }
}