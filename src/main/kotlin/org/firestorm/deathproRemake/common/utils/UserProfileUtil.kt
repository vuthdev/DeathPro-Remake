package org.firestorm.deathproRemake.common.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.extension.clogger
import java.util.UUID

object UserProfileUtil {
    fun createProfile(player: Player): UserProfile {
        val playerProfile = player.playerProfile
        val properties = playerProfile.properties
            .filter { it.name == "textures" }
            .map { TextureProperty(it.name, it.value, it.signature) }

        return UserProfile(
            UUID.randomUUID(),
            player.name,
            properties
        )
    }
}