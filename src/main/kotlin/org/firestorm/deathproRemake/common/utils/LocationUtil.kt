package org.firestorm.deathproRemake.common.utils

import org.bukkit.Location

object LocationUtil {
    fun toReadable(loc: Location) =
        "§7(§f${loc.blockX}§7, §f${loc.blockY}§7, §f${loc.blockZ}§7)"

//    fun isSafe(loc: Location): Boolean {
//
//    }
}