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