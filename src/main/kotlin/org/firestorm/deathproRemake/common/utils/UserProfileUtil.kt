package org.firestorm.deathproRemake.common.utils

import com.github.retrooper.packetevents.protocol.player.UserProfile
import org.bukkit.entity.Player
import java.util.UUID

object UserProfileUtil {
    fun createProfile(player: Player): UserProfile {
        return UserProfile(
            UUID.randomUUID(),
            player.name,
        )
    }
}