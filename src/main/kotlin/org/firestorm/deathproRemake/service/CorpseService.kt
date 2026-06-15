package org.firestorm.deathproRemake.service

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.common.utils.IDGeneratorUtil
import org.firestorm.deathproRemake.common.utils.LocationUtil
import org.firestorm.deathproRemake.common.utils.UserProfileUtil
import org.firestorm.deathproRemake.manager.CorpseManager
import org.firestorm.deathproRemake.manager.UserProfileManager
import org.firestorm.deathproRemake.model.CorpseState
import org.firestorm.deathproRemake.model.NpcSkinData
import org.firestorm.deathproRemake.packets.CorpsePacketManager

class CorpseService(override val plugin: DeathproRemake): BaseService(plugin) {

    fun spawnCorpse(player: Player) {
        val userProfile = UserProfileUtil.createProfile(player)
        val entityId = IDGeneratorUtil.generateId()
        val groundLocation = LocationUtil.calculateGroundLocation(player.location)
        lateinit var skinData: NpcSkinData

        val user = PacketEvents.getAPI().playerManager.getUser(player)
        if (user != null && user.profile != null) {
            user.profile.textureProperties.forEach {
                if(it.name == "textures") {
                    userProfile.textureProperties.add(
                        TextureProperty("textures", it.value, it.signature)
                    )
                }
            }
        }

        val createPlayerInfo = CorpsePacketManager.createPlayerInfo(player, userProfile)
        val spawnEntity = CorpsePacketManager.createSpawnPacket(player, entityId, groundLocation)
        val removePlayerInfo = CorpsePacketManager.removePlayerInfo(player, userProfile)
        val createEntityMetadata = CorpsePacketManager.createEntityData(entityId)
        val destroyEntity = CorpsePacketManager.destroyEntity(entityId)
        val hideNPCName = CorpsePacketManager.hideNPCName(userProfile)

        val applyEquipment = CorpsePacketManager.createEntityEquipment(player, entityId)

        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            playerManager.sendPacket(onlinePlayers, createPlayerInfo)
            playerManager.sendPacket(onlinePlayers, spawnEntity)
            playerManager.sendPacket(onlinePlayers, createEntityMetadata)

            // only apply if items not empty
            applyEquipment?.let { playerManager.sendPacket(onlinePlayers, it) }

            scheduler.runTaskLater(plugin, Runnable {
                playerManager.sendPacket(onlinePlayers, removePlayerInfo)
                playerManager.sendPacket(onlinePlayers, hideNPCName)
            }, 20L)

            val now = System.currentTimeMillis()
            val countdownSeconds = config.ghost.duration
            val expiredAt = now + (countdownSeconds * 1000L)

            val task = startDestroyEntity(onlinePlayers, 10,destroyEntity)

//            val corpse = CorpseState(
//                entityId,
//                player.name,
//                groundLocation,
//                skinData,
//                expiredAt,
//                task.taskId
//            )
//
//            CorpseManager.add(player.uniqueId, corpse)
        }
    }

    fun startDestroyEntity(player: Player, seconds: Int, packet: PacketWrapper<*>): BukkitTask {
        var seconds = seconds
        return scheduler.runTaskTimer(plugin, Runnable {
            when {
                seconds <= 0 -> {
                    playerManager.sendPacket(player, packet)
                }
                else -> {
                    --seconds
                }
            }
        }, 0L, 20L)
    }
}