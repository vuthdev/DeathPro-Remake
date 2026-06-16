package org.firestorm.deathproRemake.common.extension

import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.inventory.ItemStack
import org.firestorm.deathproRemake.model.PlayerEquipment

fun PlayerEquipment.fromPlayerEquipment(): List<Equipment> {
    val equipmentList = mutableListOf<Equipment>()

    val addSlot = { slot: EquipmentSlot, item: ItemStack? ->
        if (item != null && !item.type.isAir) {
            val packetItem = SpigotConversionUtil.fromBukkitItemStack(item)
            equipmentList.add(Equipment(slot, packetItem))
        }
    }

    addSlot(EquipmentSlot.MAIN_HAND, this.mainHand)
    addSlot(EquipmentSlot.OFF_HAND, this.offHand)
    addSlot(EquipmentSlot.HELMET, this.helmet)
    addSlot(EquipmentSlot.CHEST_PLATE, this.chestplate)
    addSlot(EquipmentSlot.LEGGINGS, this.leggings)
    addSlot(EquipmentSlot.BOOTS, this.boots)

    return equipmentList
}