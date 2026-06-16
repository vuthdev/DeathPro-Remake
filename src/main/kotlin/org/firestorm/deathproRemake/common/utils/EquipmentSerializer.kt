package org.firestorm.deathproRemake.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.inventory.ItemStack
import org.firestorm.deathproRemake.model.PlayerEquipment
import java.util.Base64

object EquipmentSerializer {
    private val gson = Gson()

    fun toJson(equipment: PlayerEquipment): String {
        val map = mapOf(
            "mainHand" to itemToB64(equipment.mainHand),
            "offHand" to itemToB64(equipment.offHand),
            "helmet" to itemToB64(equipment.helmet),
            "chestplate" to itemToB64(equipment.chestplate),
            "leggings" to itemToB64(equipment.leggings),
            "boots" to itemToB64(equipment.boots)
        )
        return gson.toJson(map)
    }

    fun fromJson(json: String?): PlayerEquipment {
        val type = object : TypeToken<Map<String, String?>>() {}.type
        val map: Map<String, String?> = gson.fromJson(json, type)

        return PlayerEquipment(
            mainHand = b64ToItem(map["mainHand"]),
            offHand = b64ToItem(map["offHand"]),
            helmet = b64ToItem(map["helmet"]),
            chestplate = b64ToItem(map["chestplate"]),
            leggings = b64ToItem(map["leggings"]),
            boots = b64ToItem(map["boots"])
        )
    }

    /*
     ==============================
     ====== PRIVATE FUNCTION ======
     ==============================
     */

    private fun itemToB64(item: ItemStack?): String? {
        if (item == null || item.type.isAir) return null
        return Base64.getEncoder().encodeToString(item.serializeAsBytes())
    }

    private fun b64ToItem(b64: String?): ItemStack? {
        if(b64.isNullOrEmpty()) return null
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(b64))
    }
}