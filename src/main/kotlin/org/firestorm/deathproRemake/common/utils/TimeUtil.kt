package org.firestorm.deathproRemake.common.utils

object TimeUtil {
    fun formatCountdown(seconds: Int): String =
        if (seconds >= 60) "${seconds / 60}m ${seconds % 60}s" else "${seconds}s"
}