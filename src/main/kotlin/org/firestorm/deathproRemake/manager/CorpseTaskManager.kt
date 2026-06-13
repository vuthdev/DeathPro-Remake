package org.firestorm.deathproRemake.manager

import org.bukkit.Bukkit
import org.firestorm.deathproRemake.model.CorpseState
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object CorpseTaskManager {
    private val tasks = ConcurrentHashMap<UUID, Int>()

    fun add(uuid: UUID, taskId: Int) { tasks[uuid] = taskId}
    fun remove(uuid: UUID) = tasks.remove(uuid)
    fun get(uuid: UUID) = tasks[uuid]
    fun cancel(uuid: UUID) {
        tasks[uuid]?.let {
            Bukkit.getScheduler().cancelTask(it)
        }
    }
}