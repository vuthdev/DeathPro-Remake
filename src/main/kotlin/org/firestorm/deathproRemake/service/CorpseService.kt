package org.firestorm.deathproRemake.service

import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.common.constants.DefaultSkins
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.fromPlayerEquipment
import org.firestorm.deathproRemake.common.utils.IDGeneratorUtil
import org.firestorm.deathproRemake.common.utils.LocationUtil
import org.firestorm.deathproRemake.common.utils.UserProfileUtil
import org.firestorm.deathproRemake.manager.CorpseTaskManager
import org.firestorm.deathproRemake.model.CorpseState
import org.firestorm.deathproRemake.model.NpcSkinData
import org.firestorm.deathproRemake.model.PlayerEquipment
import org.firestorm.deathproRemake.packets.CorpsePacketManager
import org.firestorm.deathproRemake.repository.CorpseRepository
import java.util.UUID

class CorpseService(override val plugin: DeathproRemake): BaseService(plugin) {

    fun spawnCorpse(player: Player) {
        val userProfile = UserProfileUtil.createProfile(player)
        val entityId = IDGeneratorUtil.generateId()
        val groundLocation = LocationUtil.calculateGroundLocation(player.location)
        val inventory = player.inventory
        val equipment = PlayerEquipment(
            mainHand = inventory.itemInMainHand,
            offHand = inventory.itemInOffHand,
            helmet = inventory.helmet,
            chestplate = inventory.chestplate,
            leggings = inventory.leggings,
            boots = inventory.boots
        )

        val texture = userProfile.textureProperties.firstOrNull { it.name == "textures" }

        val skin = if (texture != null) {
            NpcSkinData(
                texture.value,
                texture.signature
            )
        } else {
            NpcSkinData(
                DefaultSkins.STEVE_VALUE,
                DefaultSkins.STEVE_SIGNATURE
            )
        }


        val now = System.currentTimeMillis()
        val countdownSeconds = config.corpse.duration
        val expiredAt = now + (countdownSeconds * 1000L)

        val task = startDestroyEntity(entityId, countdownSeconds)

        val corpse = CorpseState(
            corpseId = entityId,
            playerName = player.name,
            playerUuid = player.uniqueId,
            location = groundLocation,
            skin = skin,
            expiredAt = expiredAt,
            equipment = equipment,
            taskId = task.taskId
        )

        CorpseRepository.insert(corpse)
        CorpseTaskManager.add(entityId, taskId = task.taskId)

        val equipmentList = equipment.fromPlayerEquipment()
        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            sendPacketEntity(onlinePlayers, equipmentList, userProfile, entityId, groundLocation)
        }
    }

    fun restoreCorpse() {
        val state = CorpseRepository.loadActive()

        state.forEach { corpse ->
            when {
                corpse.isExpired -> {
                    CorpseTaskManager.cancel(corpse.corpseId)
                    CorpseRepository.deleteExpired()
                }
                else -> {
                    val userProfile = UserProfile(
                        UUID.randomUUID(),
                        corpse.playerName,
                        listOf(
                            TextureProperty("textures", corpse.skin.skinTexture, corpse.skin.skinSignature)
                        )
                    )

                    for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                        sendPacketEntity(
                            onlinePlayer,
                            corpse.equipment.fromPlayerEquipment(),
                            userProfile,
                            corpse.corpseId,
                            corpse.location
                        )
                    }

                    if (CorpseTaskManager.isActive(corpse.corpseId)) {
                        val remainingSeconds = corpse.remainingSeconds.toInt()
                        val task = startDestroyEntity(corpse.corpseId, remainingSeconds)
                        CorpseTaskManager.update(corpse.corpseId, taskId = task.taskId)
                    }
                }
            }
        }
    }

    fun sendPacketEntity(viewerPlayer: Player, equipment: List<Equipment>, userProfile: UserProfile, entityId: Int, location: Location) {
        val createPlayerInfo = CorpsePacketManager.createPlayerInfo(userProfile)
        val spawnEntity = CorpsePacketManager.createSpawnPacket(userProfile, entityId, location)

        val pose = if (config.corpse.pose == "SWIMMING") EntityPose.SWIMMING else EntityPose.SLEEPING
        val createEntityMetadata = CorpsePacketManager.createEntityData(entityId, pose)
        val removePlayerInfo = CorpsePacketManager.removePlayerInfo(userProfile)
        val hideNPCName = CorpsePacketManager.hideNPCName(userProfile)
        val applyEquipment = CorpsePacketManager.createEntityEquipment(equipment, entityId)

        playerManager.sendPacket(viewerPlayer, createPlayerInfo)
        playerManager.sendPacket(viewerPlayer, spawnEntity)
        playerManager.sendPacket(viewerPlayer, createEntityMetadata)

        // only apply if items not empty
        applyEquipment?.let { playerManager.sendPacket(viewerPlayer, it) }

        scheduler.runTaskLater(plugin, Runnable {
            playerManager.sendPacket(viewerPlayer, removePlayerInfo)
            playerManager.sendPacket(viewerPlayer, hideNPCName)
        }, 20L)
    }

    private fun startDestroyEntity(corpseId: Int, seconds: Int): BukkitTask {
        val destroyEntity = CorpsePacketManager.destroyEntity(corpseId)

        var seconds = seconds
        return scheduler.runTaskTimer(plugin, Runnable {
            when {
                seconds <= 0 -> {
                    for (onlinePlayers in Bukkit.getOnlinePlayers()) {
                        playerManager.sendPacket(onlinePlayers, destroyEntity)
                    }
                    CorpseRepository.delete(corpseId)
                    CorpseTaskManager.cancel(corpseId)
                }
                else -> {
                    --seconds
                }
            }
        }, 0L, 20L)
    }
}