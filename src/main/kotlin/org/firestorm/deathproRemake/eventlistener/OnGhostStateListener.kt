package org.firestorm.deathproRemake.eventlistener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseListener
import org.firestorm.deathproRemake.common.extension.isGhost

class OnGhostStateListener(private val p: DeathproRemake): BaseListener(p) {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player

        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player

        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerTakeDamage(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return

        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerEntityInteract(e: PlayerInteractEntityEvent) {
        val player = e.player
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val player = e.player as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
     fun onPlayerDropItem(e: PlayerDropItemEvent) {
         val player = e.player
        if (!player.isGhost()) return
         e.isCancelled = true
     }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEntityPickupItem(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun foodLevelChange(e: FoodLevelChangeEvent) {
        val player = e.entity as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onRegainingHealth(e: EntityRegainHealthEvent) {
        val player = e.entity as? Player ?: return
        if (!player.isGhost()) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerSwingingHand(e: PlayerAnimationEvent) {
        val player = e.player
        if (!player.isGhost()) return
        e.isCancelled = true
    }
}