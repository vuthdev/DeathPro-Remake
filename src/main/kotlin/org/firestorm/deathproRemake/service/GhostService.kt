package org.firestorm.deathproRemake.service

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.manager.GhostManager
import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.repository.GhostRepository
import java.util.UUID

class GhostService(
    override val plugin: DeathproRemake,
    val ghostRepository: GhostRepository
): BaseService(plugin) {

    fun enterGhostMode(player: Player, deathLocation: Location) {
        val respawnLoc = player.respawnLocation
            ?: player.world.spawnLocation

        applyEffects(player)

        player.teleport(deathLocation.clone().add(0.0, 1.5, 0.0))

        val now = System.currentTimeMillis()
        val countdownSeconds = config.ghost.duration
        val expiredAt = now + (countdownSeconds * 1000L)

        val task = startCountdown(player, countdownSeconds)

         val ghostState = GhostState(
            playerUuid = player.uniqueId,
            playerName = player.name,
            deathLocation = deathLocation,
            respawnLocation = respawnLoc,
            taskId = task.taskId,
            expiredAt = expiredAt,
        )

        GhostManager.add(ghostState)
        ghostRepository.save(player, ghostState)

        val title = Component.text("§cYou died!")
        val subTitle = Component.text("§7Respawning in §f${countdownSeconds}s §7— press §fSHIFT §7to respawn now")

        val sendTitle = Title.title(title, subTitle, 10, 60, 20)
        player.showTitle(sendTitle)
    }

    fun restoreGhostMode(player: Player) {
        val state = ghostRepository.load(player)

        when {
            state.isExpired -> {
                ghostRepository.clear(player)
                logger.info("Clear ghost state for ${player.name}")
            }
            else -> {
                applyEffects(player)

                player.teleport(state.deathLocation)

                val remaining = state.remainingSeconds.toInt()
                val task = startCountdown(player, remaining)
                GhostManager.update(player.uniqueId, { state ->
                    state.copy(taskId = task.taskId)
                })

                player.showTitle(
                    Title.title(
                        Component.text("§cStill dead!"),
                        Component.text("§7Respawning in §f${remaining}s"),
                        10, 60, 20
                    )
                )
                logger.info("Restored ghost mode for ${player.name}, ${remaining}s remaining.")
            }
        }
    }

    fun exitGhostMode(uuid: UUID) {
        val state = GhostManager.get(uuid) ?: return
        val player = Bukkit.getPlayer(state.playerUuid) ?: return

        // Cancel countdown task
        scheduler.cancelTask(state.taskId)
        GhostManager.remove(uuid)
        ghostRepository.clear(player)

        if (player.isOp) {
            player.apply {
                allowFlight = true
                isFlying = false
                isInvisible = false
            }
        } else {
            player.apply {
                allowFlight = false
                isFlying = false
                isInvisible = false
            }
        }

        player.teleport(state.respawnLocation)
        val title = Title.title(
            Component.text("§aRespawned!"),
            Component.text(""),
            5, 20, 10
        )
        player.showTitle(title)
    }

    private fun startCountdown(player: Player, seconds: Int): BukkitTask {
        var remaining = seconds
        return scheduler.runTaskTimer(plugin, Runnable {
            when {
                remaining <= 0 -> {
                    exitGhostMode(player.uniqueId)
                }
                else -> {
                    player.sendActionBar(Component.text("§7Respawning in §f${remaining}s §7— Shift to respawn now"))
                    remaining--
                }
            }

        }, 0L, 20L)
    }

    fun applyEffects(player: Player) {
        player.apply {
            gameMode = GameMode.SURVIVAL
            allowFlight = true
            isFlying = true
            isInvisible = true
            foodLevel = 20
            saturation = 20f
            health = player.health.plus(player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0)
        }
    }
}