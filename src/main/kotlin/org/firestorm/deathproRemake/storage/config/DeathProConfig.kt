package org.firestorm.deathproRemake.storage.config

import org.firestorm.deathproRemake.DeathproRemake

class DeathProConfig(private val plugin: DeathproRemake) {

    lateinit var ghost: GhostConfig  private set
    lateinit var corpse: CorpseConfig private set
    lateinit var updateCheckerConfig: UpdateCheckerConfig private set

    init { load() }

    fun reload() {
        plugin.reloadConfig()
        load()
    }

    private fun load() {
        val cfg = plugin.config
        ghost = GhostConfig(cfg)
        corpse = CorpseConfig(cfg)
        updateCheckerConfig = UpdateCheckerConfig(cfg)
    }
}