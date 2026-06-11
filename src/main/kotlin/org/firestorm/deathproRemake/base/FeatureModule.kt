package org.firestorm.deathproRemake.base

import org.firestorm.deathproRemake.DeathproRemake

abstract class FeatureModule(protected val plugin: DeathproRemake) {
    abstract val name: String
    abstract fun onEnable()
    open fun onDisable() {}
    open fun isEnabled(): Boolean = true
}