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

        val startX = initialLoc.blockX
        val startY = initialLoc.blockY
        val startZ = initialLoc.blockZ

        var targetY = startY

        for (yOffset in 0..16) {
            val currentY = startY - yOffset
            if (currentY <= world.minHeight) break

            val block = world.getBlockAt(startX, currentY, startZ)

            if (block.type.isSolid) {
                targetY = currentY
                break
            }
        }

        val groundLoc = Location(
            world,
            initialLoc.x,
            targetY + 1.25,
            initialLoc.z,
            initialLoc.yaw,
            initialLoc.pitch
        )

        return groundLoc
    }
}