package org.firestorm.deathproRemake.base

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.firestorm.deathproRemake.DeathproRemake

abstract class BaseListener(protected val plugin: DeathproRemake) : Listener {
    fun register() = Bukkit.getPluginManager().registerEvents(this, plugin)
}