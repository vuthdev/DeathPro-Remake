package org.firestorm.deathproRemake.service

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.manager.GhostTaskManager
import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.repository.GhostRepository
import java.time.Duration
import java.util.UUID

class GhostService(
    override val plugin: DeathproRemake,
    val ghostRepository: GhostRepository
): BaseService(plugin) {

    fun enterGhostMode(player: Player, deathLocation: Location, respawnLocation: Location) {
        val now = System.currentTimeMillis()
        val countdownSeconds = config.ghost.duration
        val expiredAt = now + (countdownSeconds * 1000L)

        val task = startCountdown(player, countdownSeconds)

         val ghostState = GhostState(
            playerUuid = player.uniqueId,
            playerName = player.name,
            deathLocation = deathLocation,
            respawnLocation = respawnLocation,
            taskId = task.taskId,
            expiredAt = expiredAt,
        )

        applyGhostEffect(player, true)
//        player.teleportAsync(deathLocation.clone().add(0.0, 1.5, 0.0))

        GhostTaskManager.add(player.uniqueId, task.taskId)
        ghostRepository.save(player, ghostState)
    }

    fun restoreGhostMode(player: Player) {
        val state = ghostRepository.load(player)

        when {
            state.isExpired -> {
                ghostRepository.clear(player)
                applyGhostEffect(player, false)
                player.teleportAsync(state.respawnLocation)
            }
            else -> {
                applyGhostEffect(player, true)

                player.teleportAsync(state.deathLocation)

                val remaining = state.remainingSeconds.toInt()
                val task = startCountdown(player, remaining)
                GhostTaskManager.update(player.uniqueId, task.taskId)

                player.showTitle(
                    Title.title(
                        messageConfig.ghostTitle,
                        messageConfig.ghostSubtitle(remaining),
                        Title.Times.times(
                            Duration.ofSeconds(2),
                            Duration.ofSeconds(5),
                            Duration.ofSeconds(2)
                        )
                    )
                )
            }
        }
    }

    fun exitGhostMode(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid) ?: return
        val ghost = ghostRepository.load(player)

        // Cancel countdown task
        GhostTaskManager.cancel(uuid)
        ghostRepository.clear(player)

        applyGhostEffect(player, false)

        player.teleportAsync(ghost.respawnLocation)
        val title = Title.title(
            messageConfig.ghostRespawned,
            "".color(),
            Title.Times.times(
                Duration.ofSeconds(0),
                Duration.ofSeconds(5),
                Duration.ofSeconds(2)
            )
        )
        player.showTitle(title)
    }

    /*
     ==============================
     ====== PRIVATE FUNCTION ======
     ==============================
     */

    private fun startCountdown(player: Player, seconds: Int): BukkitTask {
        var remaining = seconds
        return scheduler.runTaskTimer(plugin, Runnable {
            when {
                remaining <= 0 -> {
                    exitGhostMode(player.uniqueId)
                }
                else -> {
                    val title = Title.title(
                        messageConfig.ghostTitle,
                        messageConfig.ghostSubtitle,
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofSeconds(0)
                        )
                    )
                    player.showTitle(title)
                    // player.sendActionBar(Component.text("§7Respawning in §f${remaining}s §7— Shift to respawn now"))
                    remaining--
                }
            }

        }, 0L, 20L)
    }

    private fun applyGhostEffect(player: Player, isApply: Boolean) {
        if (isApply) {
            player.apply {
                gameMode = GameMode.SURVIVAL
                allowFlight = true
                isFlying = true
                isInvisible = true
                isInvulnerable = true
                foodLevel = 20
                saturation = 20f
                health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
                isSilent = true
            }

            // clear mob target
            player.world.getNearbyEntities(player.location, 32.0, 32.0, 32.0)
                .filterIsInstance<Mob>()
                .forEach { mob ->
                    if (mob.target == player) mob.target = null
                }

        } else {
            if (player.isOp) {
                player.apply {
                    allowFlight = true
                    isFlying = false
                    isInvisible = false
                    isInvulnerable = false
                    isSilent = false
                }
            } else {
                player.apply {
                    allowFlight = false
                    isFlying = false
                    isInvisible = false
                    isInvulnerable = false
                    isSilent = false
                }
            }
        }
    }
}