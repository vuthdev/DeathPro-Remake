package org.firestorm.deathproRemake.common.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun String.color(): Component {
    val hasLegacy = this.contains(Regex("&[0-9a-fk-orA-FK-ORxX]"))

    return if (hasLegacy) {
        LegacyComponentSerializer.legacyAmpersand().deserialize(this)
    } else {
        MiniMessage.miniMessage().deserialize(this)
    }
}

fun String.format(vararg placeholders: Pair<String, Any>): String {
    var result = this
    placeholders.forEach { (key, value) ->
        result = result.replace("{$key}", value.toString())
    }
    return result
}

fun String.formatColor(vararg placeholders: Pair<String, Any>): Component =
    this.format(*placeholders).color()

fun String.stripColor(): String =
    LegacyComponentSerializer.legacyAmpersand()
        .deserialize(this)
        .let { net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(it) }