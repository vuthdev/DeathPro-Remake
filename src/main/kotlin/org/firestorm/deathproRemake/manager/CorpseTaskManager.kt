package org.firestorm.deathproRemake.manager

import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object CorpseTaskManager {
    private val tasks = ConcurrentHashMap<Int, Int>()

    fun add(corpseId: Int, taskId: Int) { tasks[corpseId] = taskId}
    fun remove(corpseId: Int) = tasks.remove(corpseId)
    fun get(corpseId: Int) = tasks[corpseId]
    fun isActive(corpseId: Int) = tasks.containsKey(corpseId)
    fun update(corpseId: Int, taskId: Int) { tasks[corpseId] = taskId }
    fun cancel(corpseId: Int) {
        tasks[corpseId]?.let {
            Bukkit.getScheduler().cancelTask(it)
            remove(corpseId)
        }
    }

    fun cancelAll() {
        tasks.values.forEach {
            Bukkit.getScheduler().cancelTask(it)
        }
        tasks.clear()
    }
}