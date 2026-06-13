package org.firestorm.deathproRemake.common.extension

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.firestorm.deathproRemake.DeathproRemake

inline val Any.clogger: ComponentLogger
    get() = DeathproRemake.instance.componentLogger