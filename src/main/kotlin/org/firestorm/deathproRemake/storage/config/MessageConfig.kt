package org.firestorm.deathproRemake.storage.config

import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.YamlConfiguration
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.common.constants.MessageConstants
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.common.extension.formatColor
import java.io.File

class MessageConfig(private val plugin: DeathproRemake) {

    private val file = File(plugin.dataFolder, "messages.yml")
    private lateinit var cfg: YamlConfiguration

    init {
        if (!file.exists()) plugin.saveResource("messages.yml", false)
        load()
    }

    fun reload() = load()

    private fun load() {
        cfg = YamlConfiguration.loadConfiguration(file)
    }

    val ghostTitle get() = bare(MessageConstants.Ghost.TITLE)
    val ghostSubtitle get() = bare(MessageConstants.Ghost.SUBTITLE)
    val ghostRespawned get() = bare(MessageConstants.Ghost.RESPAWNED)
    val ghostCantInteract get() = get(MessageConstants.Ghost.CANT_INTERACT)
    val ghostCantCommand get() = get(MessageConstants.Ghost.CANT_COMMAND)

    fun ghostSubtitle(time: Int) = bare(MessageConstants.Ghost.SUBTITLE, "time" to time)

    private fun raw(key: String, default: String = ""): String =
        cfg.getString(key, default) ?: default

    // returns Component — with prefix
    fun get(key: String, vararg placeholders: Pair<String, Any>): Component =
        (rawPrefix() + raw(key)).formatColor(*placeholders)

    // returns Component — no prefix
    fun bare(key: String, vararg placeholders: Pair<String, Any>): Component =
        raw(key).formatColor(*placeholders)

    // returns Component — no prefix, custom default
    fun bareOrDefault(key: String, default: String): Component =
        raw(key, default).color()

    // raw string prefix (needed for concatenation before colorizing)
    fun rawPrefix(): String =
        raw(MessageConstants.PREFIX, "&f[&c&lDEATHPRO&f]")
}