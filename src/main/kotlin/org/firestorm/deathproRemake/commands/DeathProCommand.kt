package org.firestorm.deathproRemake.commands

import org.firestorm.deathproRemake.annotations.RegisterCommand
import org.firestorm.deathproRemake.annotations.SubCommand

@RegisterCommand(
    name = "deathpro",
    aliases = ["dp"],
    usage = "/deathpro",
)
class DeathProCommand {

    @SubCommand
    fun default() {

    }

    @SubCommand(name = "help")
    fun help() {

    }

    @SubCommand(name = "spawncorpse", playerOnly = true)
    fun spawnCorpse() {

    }
}