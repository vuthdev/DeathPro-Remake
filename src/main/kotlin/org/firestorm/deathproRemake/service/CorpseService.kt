package org.firestorm.deathproRemake.service

import com.github.retrooper.packetevents.wrapper.PacketWrapper
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.common.utils.IDGeneratorUtil
import org.firestorm.deathproRemake.common.utils.UserProfileUtil
import org.firestorm.deathproRemake.packets.CorpsePacketManager

class CorpseService(override val plugin: DeathproRemake): BaseService(plugin) {

    fun spawnCorpse(player: Player) {
        val userProfile = UserProfileUtil.createProfile(player)
        val entityId = IDGeneratorUtil.generateId()

        val createPlayerInfo = CorpsePacketManager.createPlayerInfo(player, userProfile)
        val spawnEntity = CorpsePacketManager.createSpawnPacket(player, userProfile, entityId)
        val removePlayerInfo = CorpsePacketManager.removePlayerInfo(player, userProfile)
        val createEntityMetadata = CorpsePacketManager.createEntityData(entityId)
        val destroyEntity = CorpsePacketManager.destroyEntity(entityId)
        val hideNPCName = CorpsePacketManager.hideNPCName(userProfile)
        val applyEquipment = CorpsePacketManager.createEntityEquipment(player, entityId)

        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            playerManager.sendPacket(onlinePlayers, createPlayerInfo)
            playerManager.sendPacket(onlinePlayers, spawnEntity)
            playerManager.sendPacket(onlinePlayers, createEntityMetadata)

            // only send packet if it not null
            applyEquipment?.let {
                playerManager.sendPacket(onlinePlayers, applyEquipment)
            }

            scheduler.runTaskLater(plugin, Runnable {
                playerManager.sendPacket(onlinePlayers, removePlayerInfo)
                playerManager.sendPacket(onlinePlayers, hideNPCName)
            }, 20L)

            startDestroyEntity(onlinePlayers, destroyEntity)
        }
    }

    fun startDestroyEntity(player: Player, packet: PacketWrapper<*>) {
        var seconds = 10
        scheduler.runTaskTimer(plugin, Runnable {
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