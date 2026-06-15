package org.firestorm.deathproRemake.manager

import com.github.retrooper.packetevents.protocol.player.UserProfile
import java.util.UUID

object UserProfileManager {
    val profiles = mutableMapOf<UUID, UserProfile>()

    fun add(uuid: UUID, profile: UserProfile) { profiles[uuid] = profile }
    fun update(uuid: UUID, profile: UserProfile) { profiles[uuid] = profile }
    fun get(uuid: UUID) = profiles[uuid]
    fun remove(uuid: UUID) = profiles.remove(uuid)
}