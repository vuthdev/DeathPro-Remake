package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration

class CorpseConfig(config: FileConfiguration) : ConfigSection(config, "corpse") {
    val enabled get() = boolean("enabled", true)
    val duration get() = int("duration", 300)
}