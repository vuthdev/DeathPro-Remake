package org.firestorm.deathproRemake.base

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import org.firestorm.deathproRemake.DeathproRemake

abstract class BasePacketListener : PacketListenerAbstract() {
    fun register() = PacketEvents.getAPI().eventManager.registerListener(this)
}