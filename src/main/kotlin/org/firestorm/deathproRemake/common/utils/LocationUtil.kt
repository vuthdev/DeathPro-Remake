package org.firestorm.deathproRemake.common.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

object LocationUtil {
    fun toReadable(loc: Location) =
        "§7(§f${loc.blockX}§7, §f${loc.blockY}§7, §f${loc.blockZ}§7)"

    fun isSafe(loc: Location): Boolean {
        val feet = loc.block
        val head = feet.getRelative(0 ,1 ,0)
        val ground = feet.getRelative(0, -1, 0)

        if (!ground.type.isSolid || ground.isLiquid) {
            return false
        }

        if (!isBlockSafeToStandIn(feet) || !isBlockSafeToStandIn(head)) {
            return false
        }

        return true
    }

    fun isBlockSafeToStandIn(block: Block): Boolean {
        val type: Material = block.type

        return !type.isSolid
                && type != Material.LAVA
                && type != Material.FIRE
                && type != Material.SOUL_FIRE
    }

    fun calculateGroundLocation(initialLoc: Location): Location {
        val world = initialLoc.world ?: return initialLoc

        val highestBlock = world.getHighestBlockAt(initialLoc.blockX, initialLoc.blockZ)

        val groundLoc = highestBlock.location.apply {
            yaw = initialLoc.yaw
            pitch = initialLoc.pitch
        }

        groundLoc.y = highestBlock.y + 1.25

        return groundLoc
    }
}