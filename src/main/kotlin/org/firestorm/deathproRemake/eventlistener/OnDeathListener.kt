package org.firestorm.deathproRemake.eventlistener

import com.google.common.collect.ImmutableSet
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.manager.GhostRespawnPending
import org.firestorm.deathproRemake.repository.PendingDeathInvRepository
import java.util.UUID
import kotlin.text.clear

class OnDeathListener(
    private val p: DeathproRemake,
): BaseListener(p) {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player: Player = e.player

        val location = player.location
        val deathLocation = location.clone().apply {
            y = location.y + 1
        }

        GhostRespawnPending.add(player.uniqueId, deathLocation)

        // spawn npc corpse
        p.corpseService.spawnCorpse(player)

        val keepInventory = player.world.getGameRuleValue(GameRule.KEEP_INVENTORY)

        // clear player's inv and saved
        if (keepInventory == true) {
            val playerInv = player.inventory.contents.map { it?.clone() }.toTypedArray()

            // save player's inventory
            PendingDeathInvRepository.insert(player.uniqueId, playerInv)

            // prevent player's drop
            e.drops.clear()

            // clear player inventory
            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)
            player.inventory.setItemInOffHand(null)
        }

        scheduler.runTaskLater(plugin, Runnable {
            // force respawn
            player.spigot().respawn()
        }, 1L)
    }

//    fun getRespawnLocation(player: Player): Location {
//        val spawnLocation = player.respawnLocation
//        val isBedSpawn = spawnLocation != null
//        val defaultLoc = spawnLocation ?: player.world.spawnLocation
//        val respawnFlag = ImmutableSet.Builder<PlayerRespawnEvent.RespawnFlag>()
//            .add(PlayerRespawnEvent.RespawnFlag.BED_SPAWN)
//
//        val respawnEvent = PlayerRespawnEvent(
//            player,
//            defaultLoc,
//            isBedSpawn,
//            false,
//            PlayerRespawnEvent.RespawnReason.PLUGIN,
//            respawnFlag
//        )
//        Bukkit.getPluginManager().callEvent(respawnEvent)
//
//        return respawnEvent.respawnLocation
//    }
}