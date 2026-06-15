package org.firestorm.deathproRemake.common.utils

import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicInteger

object IDGeneratorUtil {
    private val currentId = AtomicInteger(-1000)

    fun generateId(): Int {
        return currentId.getAndDecrement()
    }
}