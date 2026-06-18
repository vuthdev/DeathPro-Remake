package org.firestorm.deathproRemake.manager

import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GhostTaskManager {
    private val tasks = ConcurrentHashMap<UUID, Int>()

    fun add(uuid: UUID, taskId: Int) { tasks[uuid] = taskId }
    fun remove(uuid: UUID) = tasks.remove(uuid)
    fun get(uuid: UUID) = tasks[uuid]
    fun update(uuid: UUID, taskId: Int) { tasks[uuid] = taskId }
    fun cancel(uuid: UUID) {
        tasks[uuid]?.let {
            Bukkit.getScheduler().cancelTask(it)
            remove(uuid)
        }
    }

    fun cancelAll() {
        tasks.values.forEach {
            Bukkit.getScheduler().cancelTask(it)
        }
        tasks.clear()
    }
}