package org.firestorm.deathproRemake.service

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
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.manager.GhostTaskManager
import org.firestorm.deathproRemake.model.GhostState
import org.firestorm.deathproRemake.repository.PdcGhostRepository
import java.time.Duration
import java.util.UUID

class GhostService(override val plugin: DeathproRemake): BaseService(plugin) {

    fun enterGhostMode(player: Player, deathLocation: Location, respawnLocation: Location) {
        val countdownSeconds = config.ghost.duration

        val ghostState = GhostState(
            playerUuid = player.uniqueId,
            playerName = player.name,
            deathLocation = deathLocation,
            respawnLocation = respawnLocation,
            remainingSeconds = countdownSeconds,
        )
        applyGhostEffect(player, true)
//        player.teleportAsync(deathLocation.clone().add(0.0, 1.5, 0.0))

        val task = startCountdown(player, ghostState)
        GhostTaskManager.add(player.uniqueId, task.taskId)
        PdcGhostRepository.save(player, ghostState)
    }

    fun pauseGhostMode(player: Player, currentRemaining: Long) {
        GhostTaskManager.cancel(player.uniqueId)

        val state = PdcGhostRepository.load(player)
        PdcGhostRepository.save(player, state.copy(remainingSeconds = currentRemaining))
    }

    fun restoreGhostMode(player: Player) {
        val state = PdcGhostRepository.load(player)

        when {
            state.isExpired -> {
                PdcGhostRepository.clear(player)
                applyGhostEffect(player, false)
                player.teleportAsync(state.respawnLocation)
                clogger.info("ghost mode expired")
            }
            else -> {
                applyGhostEffect(player, true)

                player.teleportAsync(state.deathLocation)

                val task = startCountdown(player, state)
                GhostTaskManager.update(player.uniqueId, task.taskId)

                val remaining = state.remainingSeconds.toInt()
                val title = messageConfig.ghostTitle
                val subTitle = messageConfig.ghostSubtitle(remaining)

                clogger.info("ghost mode restored")

                player.showTitle(
                    Title.title(
                        title,
                        subTitle,
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
        val ghost = PdcGhostRepository.load(player)

        // Cancel countdown task
        GhostTaskManager.cancel(uuid)
        PdcGhostRepository.clear(player)

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
        clogger.info("exit ghost mode")
        player.showTitle(title)
    }

    /*
     ==============================
     ====== PRIVATE FUNCTION ======
     ==============================
     */

    private fun startCountdown(player: Player, state: GhostState): BukkitTask {
        var remaining = state.remainingSeconds
        return scheduler.runTaskTimer(plugin, Runnable {
            if (!player.isOnline) return@Runnable

            if (remaining <= 0) {
                exitGhostMode(player.uniqueId)
                clogger.info("ghost mode ${player.name} remaining $remaining")
                return@Runnable
            }

            val title = Title.title(
                messageConfig.ghostTitle,
                messageConfig.ghostSubtitle(remaining.toInt()),
                Title.Times.times(
                    Duration.ofSeconds(0),
                    Duration.ofSeconds(3),
                    Duration.ofSeconds(0)
                )
            )
            player.showTitle(title)
            // player.sendActionBar(Component.text("§7Respawning in §f${remaining}s §7— Shift to respawn now"))

            clogger.info("ghost mode timer")
            // save current record
            val current = PdcGhostRepository.load(player)
            PdcGhostRepository.save(player, current.copy(remainingSeconds = remaining))

            remaining--
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