package org.firestorm.deathproRemake.base

import org.bukkit.Bukkit
import org.firestorm.deathproRemake.DeathproRemake

abstract class BaseService(protected val plugin: DeathproRemake) {
    protected val logger get() = plugin.logger
    protected val config get() = plugin.deathProConfig
    protected val scheduler get() = Bukkit.getScheduler()
}