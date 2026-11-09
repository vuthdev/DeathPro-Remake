package org.firestorm.deathproRemake.service

import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.base.BaseService
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.constants.DefaultSkins
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
import org.firestorm.deathproRemake.storage.database.DbExecutor
import java.time.Duration
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

        val countdownSeconds = config.corpse.duration

        val corpse = CorpseState(
            corpseId = entityId,
            playerName = player.name,
            playerUuid = player.uniqueId,
            location = groundLocation,
            skin = skin,
            remainingSeconds = countdownSeconds,
            equipment = equipment,
            expiresAt = null
        )

        DbExecutor.submit { CorpseRepository.insert(corpse) }

        val task = startDestroyEntity(player, corpse)
        CorpseTaskManager.add(corpse.corpseId, taskId = task.taskId)

        val equipmentList = equipment.fromPlayerEquipment()
        for (onlinePlayers in Bukkit.getOnlinePlayers()) {
            sendPacketEntity(onlinePlayers, equipmentList, userProfile, corpse.corpseId, groundLocation)
        }
    }

    fun restoreCorpse(player: Player) {
        val state = CorpseRepository.loadActive()

        state.forEach { corpse ->
            when {
                corpse.isExpired -> {
                    CorpseTaskManager.cancel(corpse.corpseId)
                    DbExecutor.submit { CorpseRepository.delete(corpse.corpseId) }
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

                    val task = startDestroyEntity(player, corpse)
                    if (CorpseTaskManager.isActive(corpse.corpseId)) {
                        CorpseTaskManager.update(corpse.corpseId, taskId = task.taskId)
                    } else {
                        CorpseTaskManager.add(corpse.corpseId, taskId = task.taskId)
                    }
                }
            }
        }
    }

    fun sendPacketEntity(viewerPlayer: Player, equipment: List<Equipment>, userProfile: UserProfile, entityId: Int, location: Location) {
        val pose = if (config.corpse.pose == "SWIMMING") EntityPose.SWIMMING else EntityPose.SLEEPING
        val createEntityMetadata = CorpsePacketManager.createEntityData(entityId, pose)
        val createPlayerInfo = CorpsePacketManager.createPlayerInfo(userProfile)

        val npcName = "npc_$entityId"
        val npcProfile = UserProfile(
            UUID.randomUUID(),              // fresh UUID per corpse so clients don't reuse a cached name
            npcName,
            userProfile.textureProperties   // keeps the skin
        )

        val yOffset = when (pose) {
            EntityPose.SWIMMING  -> -0.4
            else                 -> 0.0
        }

        val locationForPose = location.clone().apply {
            y += yOffset
        }

        val spawnEntity = CorpsePacketManager.createSpawnPacket(userProfile, entityId, locationForPose)

        val removePlayerInfo = CorpsePacketManager.removePlayerInfo(userProfile)
        val hideNPCName = CorpsePacketManager.hideNPCName(npcName, npcName)
        val applyEquipment = CorpsePacketManager.createEntityEquipment(equipment, entityId)

        playerManager.sendPacket(viewerPlayer, createPlayerInfo)
        playerManager.sendPacket(viewerPlayer, spawnEntity)
        playerManager.sendPacket(viewerPlayer, createEntityMetadata)

        // only apply if items not empty
        applyEquipment?.let { playerManager.sendPacket(viewerPlayer, it) }

        scheduler.runTaskLater(plugin, Runnable {
            playerManager.sendPacket(viewerPlayer, removePlayerInfo)
            playerManager.sendPacket(viewerPlayer, hideNPCName)
        }, 10L)
    }

    private fun startDestroyEntity(player: Player, state: CorpseState): BukkitTask {
        val corpseId = state.corpseId
        val ownerUuid = state.playerUuid
        var remaining = state.remainingSeconds
        val removeTeam = CorpsePacketManager.removeNPCNameTeam("npc_$corpseId")
        val destroyEntity = CorpsePacketManager.destroyEntity(corpseId)
        lateinit var task: BukkitTask

        task = scheduler.runTaskTimer(plugin, Runnable {
//            val currentPlayer = Bukkit.getPlayer(ownerUuid)
//
//            if (currentPlayer == null || !currentPlayer.isOnline) {
//                task.cancel()
//                return@Runnable
//            }

            if (remaining <= 0L) {
                for (onlinePlayers in Bukkit.getOnlinePlayers()) {
                    playerManager.sendPacket(onlinePlayers, destroyEntity)
                    playerManager.sendPacket(onlinePlayers, removeTeam)
                }
                CorpseRepository.delete(corpseId)
                CorpseTaskManager.remove(corpseId)
                task.cancel()
                return@Runnable
            }

            // save record in pdc
            //PdcCorpseRepository.save(player, remaining)
            if (remaining % BaseConstants.SAVE_INTERVAL_SECONDS == 0L) {
                val snapshot = remaining
                DbExecutor.submit {
                    CorpseRepository.updateRemainingSeconds(corpseId, ownerUuid, snapshot)
                }
            }

            --remaining
        }, 0L, 20L)

        return task
    }
}