package org.firestorm.deathproRemake.model

import org.bukkit.inventory.ItemStack

data class PlayerEquipment(
    var mainHand: ItemStack?,
    var offHand: ItemStack?,
    var helmet: ItemStack?,
    var chestplate: ItemStack?,
    var leggings: ItemStack?,
    var boots: ItemStack?
)
