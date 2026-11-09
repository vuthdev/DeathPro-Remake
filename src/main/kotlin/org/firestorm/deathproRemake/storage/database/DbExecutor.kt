package org.firestorm.deathproRemake.storage.database

import org.firestorm.deathproRemake.common.extension.clogger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DbExecutor {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "deathpro-db").apply { isDaemon = true }
    }

    fun submit(task: () -> Unit) {
        executor.execute {
            try {
                task()
            } catch (e: Exception) {
                clogger.info("failed to execute task $task", e)
                e.printStackTrace()
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }

    fun awaitTermination(timeout: Long, unit: TimeUnit) {
        executor.awaitTermination(timeout, unit)
    }
}