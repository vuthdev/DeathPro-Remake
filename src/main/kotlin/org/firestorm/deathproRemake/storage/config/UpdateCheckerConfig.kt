package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration

class UpdateCheckerConfig(config: FileConfiguration) : ConfigSection(config, "update-checker") {
    val enabled get() = boolean("enabled", true)
    val notifyOps get() = boolean("notify-ops", true)
}