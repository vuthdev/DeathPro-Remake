package org.firestorm.deathproRemake.common.utils

import java.security.SecureRandom

object IDGeneratorUtil {
    private val random = SecureRandom()

    fun generateId(): Int {
        return random.nextInt(900_000_000) + 100_000_000
    }
}