package org.firestorm.deathproRemake.storage

import org.bukkit.Bukkit
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.service.GhostService
import org.firestorm.deathproRemake.service.GraveService

class StateReloader(
    private val plugin: DeathproRemake,
    private val storage: YamlDataStorage,
    private val ghostService: GhostService,
    private val graveService: GraveService,
) {
    fun reload() {
        reloadGhosts()
        reloadGraves()
    }

    private fun reloadGhosts() {
        val states = storage.loadAllGhostStates()
        plugin.logger.info("Reloading ${states.size} ghost state(s)...")

        for (state in states) {
            when {
                // Already expired while server was down — just clean up
                state.isExpired -> {
                    plugin.logger.info("Ghost state for ${state.playerName} already expired, removing.")
                    storage.removeGhostState(state.playerUuid)
                }

                // Player is online right now (joined before reloader ran)
                else -> {
                    val player = Bukkit.getPlayer(state.playerUuid)
                    if (player != null) {
                        // Player online — restore ghost mode with remaining time
                        ghostService.restoreGhostMode(player, state)
                    } else {
                        // Player still offline — keep in storage,
                        // GhostListener.onJoin will pick it up when they connect
                        plugin.logger.info("Ghost state for ${state.playerName} saved, will restore on join.")
                    }
                }
            }
        }
    }

    private fun reloadGraves() {
        val states = storage.loadAllGraveStates()
        plugin.logger.info("Reloading ${states.size} grave(s)...")

        for (state in states) {
            when {
                state.isExpired -> {
                    graveService.expireGrave(state, reason = ExpireReason.SERVER_RESTART)
                    storage.removeGraveState(state.uuid)
                }
                else -> {
                    // Re-schedule the expiry task with remaining time
                    graveService.scheduleExpiry(state)
                }
            }
        }
    }
}