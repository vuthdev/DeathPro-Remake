package org.firestorm.deathproRemake.manager

import com.github.retrooper.packetevents.protocol.player.UserProfile
import java.util.UUID

object CorpseManager {
    val corpses = mutableMapOf<UUID, UserProfile>()

    fun addCorpse(uuid: UUID, userProfile: UserProfile) {
        corpses[uuid] = userProfile
    }
    fun getCorpse(uuid: UUID) = corpses[uuid]
    fun removeCorpse(uuid: UUID) = corpses.remove(uuid)
}