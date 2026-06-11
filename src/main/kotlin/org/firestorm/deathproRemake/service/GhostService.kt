package org.firestorm.deathproRemake.service

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.model.GhostState

class GhostService(private val plugin: DeathproRemake) {

    fun enterGhostMode(player: Player, deathLocation: Location) {
        val respawnLoc = player.respawnLocation
            ?: player.world.spawnLocation   // fallback to world spawn

        player.apply {
            gameMode = GameMode.SURVIVAL
            allowFlight = true
            isFlying = true
            isInvisible = true              // optional, toggle in config
            foodLevel = 20                  // prevent hunger
            saturation = 20f
            health = player.health.plus(player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0)       // don't let them die again instantly
        }

        // Teleport to death spot so they can watch from there
        player.teleport(deathLocation.clone().add(0.0, 1.5, 0.0))

        // Start countdown
        val countdownSeconds = plugin.deathProConfig.ghostDuration  // e.g. 10
        val task = startCountdown(player, countdownSeconds)

        GhostManager.add(
            GhostState(
                player = player,
                deathLocation = deathLocation,
                respawnLocation = respawnLoc,
                taskId = task.taskId,
                remainingSeconds = countdownSeconds,
            )
        )

        player.sendTitle(
            "§cYou died!",
            "§7Respawning in §f${countdownSeconds}s §7— press §fSHIFT §7to respawn now",
            10, 60, 20
        )
    }

    fun exitGhostMode(uuid: UUID) {
        val state = GhostManager.get(uuid) ?: return
        val player = state.player

        // Cancel countdown task
        Bukkit.getScheduler().cancelTask(state.taskId)
        GhostManager.remove(uuid)

        player.apply {
            allowFlight = false
            isFlying = false
            isInvisible = false
        }

        player.teleport(state.respawnLocation)
        player.sendTitle("§aRespawned!", "", 5, 20, 10)
    }

    private fun startCountdown(player: Player, seconds: Int): BukkitTask {
        var remaining = seconds
        return Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (!player.isOnline) {
                exitGhostMode(player.uniqueId)
                return@Runnable
            }
            if (remaining <= 0) {
                exitGhostMode(player.uniqueId)
                return@Runnable
            }
            // Update action bar every tick feels spammy, do every second
            player.sendActionBar("§7Respawning in §f${remaining}s §7— Shift to respawn now")
            remaining--
        }, 0L, 20L) // every 20 ticks = 1 second
    }
}