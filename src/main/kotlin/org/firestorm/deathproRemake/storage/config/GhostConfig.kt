package org.firestorm.deathproRemake.storage.config

import org.bukkit.configuration.file.FileConfiguration

class GhostConfig(config: FileConfiguration) : ConfigSection(config, "ghost") {
    val enabled get() = boolean("enabled", true)
    val duration get() = int("duration", 30)
    val invisible get() = boolean("invisible", true)
    val allowChat get() = boolean("allow-chat", true)
    val allowedCommands get() = stringList("allowed-commands")
}