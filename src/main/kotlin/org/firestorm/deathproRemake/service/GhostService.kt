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
import java.util.UUID

class GhostService(override val plugin: DeathproRemake): BaseService(plugin) {

    fun enterGhostMode(player: Player, deathLocation: Location) {
        val respawnLoc = player.respawnLocation
            ?: player.world.spawnLocation

        player.apply {
            gameMode = GameMode.SURVIVAL
            allowFlight = true
            isFlying = true
            isInvisible = true
            foodLevel = 20
            saturation = 20f
            health = player.health.plus(player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0)       // don't let them die again instantly
        }

        // Teleport to death spot so they can watch from there
        player.teleport(deathLocation.clone().add(0.0, 1.5, 0.0))

        // Start countdown
        val countdownSeconds = config.ghost.duration  // e.g. 10
        val task = startCountdown(player, countdownSeconds)

        GhostManager.add(
            GhostState(
                playerUuid = player.uniqueId,
                playerName = player.name,
                deathLocation = deathLocation,
                respawnLocation = respawnLoc,
                taskId = task.taskId,
                expiresAt = 5,
            )
        )

        val title = Component.text("§cYou died!")
        val subTitle = Component.text("§7Respawning in §f${countdownSeconds}s §7— press §fSHIFT §7to respawn now")

        val sendTitle = Title.title(title, subTitle, 10, 60, 20)
        player.showTitle(sendTitle)
    }

    fun exitGhostMode(uuid: UUID) {
        val state = GhostManager.get(uuid) ?: return
        val player = Bukkit.getPlayer(state.playerUuid)!!

        // Cancel countdown task
        Bukkit.getScheduler().cancelTask(state.taskId)
        GhostManager.remove(uuid)

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
            if (!player.isOnline) {
                exitGhostMode(player.uniqueId)
                return@Runnable
            }
            if (remaining <= 0) {
                exitGhostMode(player.uniqueId)
                return@Runnable
            }
            // Update action bar every tick feels spammy, do every second
            player.sendActionBar(Component.text("§7Respawning in §f${remaining}s §7— Shift to respawn now"))
            remaining--
        }, 0L, 20L) // every 20 ticks = 1 second
    }
}