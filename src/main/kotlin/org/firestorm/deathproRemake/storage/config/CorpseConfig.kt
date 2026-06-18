package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration

class CorpseConfig(config: FileConfiguration) : ConfigSection(config, "corpse") {
    val pose get() = string("pose", "SLEEPING")
    val duration get() = long("duration", 10)
}