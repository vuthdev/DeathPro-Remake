package org.firestorm.deathproRemake.eventlistener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage
import org.firestorm.deathproRemake.base.BasePacketListener

class SpawnMessageSuppressor: BasePacketListener() {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType != PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) return

        val packet = WrapperPlayServerSystemChatMessage(event)

        // the vanilla key is "block.minecraft.spawn.not_valid"
        val message = packet.message.toString()
        if (message.contains("spawn.not_valid") ||
            message.contains("no home bed") ||
            message.contains("You have no home bed")) {
            event.isCancelled = true
        }
    }
}