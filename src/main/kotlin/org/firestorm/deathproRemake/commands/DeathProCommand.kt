package org.firestorm.deathproRemake.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.annotations.RegisterCommand
import org.firestorm.deathproRemake.annotations.SubCommand
import org.firestorm.deathproRemake.service.CorpseService

@RegisterCommand(
    name = "deathpro",
    aliases = ["dp"],
    usage = "/deathpro",
)
class DeathProCommand(
    val corpseService: CorpseService
) {

    @SubCommand
    fun default(sender: CommandSender) {
        sender.sendMessage("/deathpro - list all command")
    }

    @SubCommand(name = "help")
    fun help(sender: CommandSender) {
        sender.sendMessage("/help - list all command")
    }

    @SubCommand(name = "spawncorpse", playerOnly = true)
    fun spawnCorpse(sender: CommandSender) {
        val player = sender as? Player ?: return
        corpseService.spawnCorpse(player)
    }
}