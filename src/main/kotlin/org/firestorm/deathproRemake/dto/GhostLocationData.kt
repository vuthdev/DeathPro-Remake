package org.firestorm.deathproRemake.dto

import org.bukkit.Location

data class GhostLocationData(
    val deadLocation: Location,
    val respawnLocation: Location,
)