package org.firestorm.deathproRemake.packets

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location

object CorpsePacketManager {
    private val serverVersion = PacketEvents.getAPI().serverManager.version

    fun createPlayerInfo(userProfile: UserProfile): PacketWrapper<*> {
        val addPacket = if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
            WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                listOf(getModernPlayerInfoData(userProfile))
            )
        } else {
            WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
                getLegacyPlayerInfoData(userProfile)
            )
        }

        return addPacket
    }

    fun createSpawnPacket(userProfile: UserProfile, entityId: Int, location: Location): PacketWrapper<*> {
        val location = SpigotConversionUtil.fromBukkitLocation(location)

        val packet = if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
            WrapperPlayServerSpawnEntity(entityId, userProfile.uuid, EntityTypes.PLAYER, location, location.yaw, 0, null)
        } else {
            WrapperPlayServerSpawnPlayer(entityId, userProfile.uuid, location)
        }

        return packet
    }

    fun createEntityData(entityId: Int): PacketWrapper<*> {
        val sleepingPost = EntityPose.SLEEPING
        val allLayersBitmask = 127.toByte()

        val skinIndex = if(serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_9)) 16 else 17

        val metaDataItems = listOf<EntityData<*>>(
            EntityData(0, EntityDataTypes.BYTE, 0.toByte()),
            EntityData(6, EntityDataTypes.ENTITY_POSE,sleepingPost),
            EntityData(skinIndex, EntityDataTypes.BYTE, allLayersBitmask)
        )

        val metadata = WrapperPlayServerEntityMetadata(entityId, metaDataItems)
        return metadata
    }

    fun createEntityEquipment(equipmentList: List<Equipment>, entityId: Int): PacketWrapper<*>? {
        if (equipmentList.isEmpty()) return null
        return WrapperPlayServerEntityEquipment(entityId, equipmentList)
    }

    fun hideNPCName(userProfile: UserProfile): PacketWrapper<*> {
        val teamName = "${userProfile.name}-${userProfile.uuid}"
        val npcName = userProfile.name

        val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.text(teamName, NamedTextColor.WHITE),
            null,
            null,
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.ALWAYS,
            null,
            WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE
        )

        val createTeamPacket = WrapperPlayServerTeams(
            teamName,
            WrapperPlayServerTeams.TeamMode.CREATE,
            teamInfo,
            npcName
        )

        return createTeamPacket
    }

    fun removePlayerInfo(userProfile: UserProfile): PacketWrapper<*> {
        val playerInfo = if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
            WrapperPlayServerPlayerInfoRemove(userProfile.uuid)
        } else {
            WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, getLegacyPlayerInfoData(userProfile))
        }

        return playerInfo
    }

    fun destroyEntity(entityId: Int): PacketWrapper<*> {
        val destroyEntityPacket = WrapperPlayServerDestroyEntities(entityId)
        return destroyEntityPacket
    }

    /*
     ==============================
     ====== PRIVATE FUNCTION ======
     ==============================
     */

    private fun getModernPlayerInfoData(userProfile: UserProfile): WrapperPlayServerPlayerInfoUpdate.PlayerInfo {
        return WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
            userProfile, true, 1, GameMode.CREATIVE, Component.text(userProfile.name), null
        )
    }

    private fun getLegacyPlayerInfoData(userProfile: UserProfile): WrapperPlayServerPlayerInfo.PlayerData {
        return WrapperPlayServerPlayerInfo.PlayerData(
            Component.text(userProfile.name), userProfile, GameMode.CREATIVE, 0
        )
    }
}