package org.firestorm.deathproRemake.eventlistener

import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import org.firestorm.deathproRemake.base.BasePacketListener
import org.firestorm.deathproRemake.common.extension.clogger

class CheckIndexListener: BasePacketListener() {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == PacketType.Play.Server.ENTITY_METADATA) {
            val wrapper = WrapperPlayServerEntityMetadata(event)
            // If the entity ID matches a real player on your server,
            // print out all the active indexes and types the server is naturally sending!
            wrapper.entityMetadata.forEach { item ->
                clogger.info("Index: ${item.index} | Type: ${item.type}")
            }
        }
    }
}