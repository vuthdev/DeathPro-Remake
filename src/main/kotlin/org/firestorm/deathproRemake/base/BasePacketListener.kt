package org.firestorm.deathproRemake.base

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import org.firestorm.deathproRemake.DeathproRemake

abstract class BasePacketListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {
    fun register() = PacketEvents.getAPI().eventManager.registerListener(this)
}