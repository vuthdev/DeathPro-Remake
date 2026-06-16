package org.firestorm.deathproRemake.storage.config.messageconfig

import org.bukkit.configuration.file.YamlConfiguration
import org.firestorm.deathproRemake.DeathproRemake
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

    val prefix        get() = bare(MessageConsants.PREFIX, default = "&f[&c&lDEATHPRO&f] ")

    // ── Ghost ──────────────────────────────────────────────────────────────
    val ghostTitle        get() = bare(MessageKeys.Ghost.TITLE)
    val ghostSubtitle     get() = bare(MessageKeys.Ghost.SUBTITLE)
    val ghostActionBar    get() = bare(MessageKeys.Ghost.ACTION_BAR)
    val ghostRespawned    get() = get(MessageKeys.Ghost.RESPAWNED)
    val ghostCantInteract get() = get(MessageKeys.Ghost.CANT_INTERACT)
    val ghostCantCommand  get() = get(MessageKeys.Ghost.CANT_COMMAND)

    fun ghostSubtitle(time: Long)  = bare(MessageKeys.Ghost.SUBTITLE,   "time" to time)
    fun ghostActionBar(time: Long) = bare(MessageKeys.Ghost.ACTION_BAR, "time" to time)

    // ── Corpse ─────────────────────────────────────────────────────────────
    val corpseExpired get() = get(MessageKeys.Corpse.EXPIRED)
    val corpseRemoved get() = get(MessageKeys.Corpse.REMOVED)

    fun corpseSpawned(location: String) = get(MessageKeys.Corpse.SPAWNED, "location" to location)

    // ── Core helpers ───────────────────────────────────────────────────────

    fun get(key: String, default: String = "", vararg placeholders: Pair<String, Any>): String {
        val raw = cfg.getString(key, default) ?: default
        return (prefix + raw).format(*placeholders)
    }

    fun bare(key: String, default: String = "", vararg placeholders: Pair<String, Any>): String {
        val raw = cfg.getString(key, default) ?: default
        return raw.format(*placeholders)
    }
}