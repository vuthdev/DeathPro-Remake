package org.firestorm.deathproRemake.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.annotations.RegisterCommand
import org.firestorm.deathproRemake.annotations.SubCommand
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.service.CorpseService

@RegisterCommand(
    name = "deathpro",
    aliases = ["dp"],
    usage = "/deathpro",
    permission = "deathpro.use"
)
class DeathProCommand(
    val p: DeathproRemake,
    val corpseService: CorpseService
) {

    @SubCommand
    fun default(sender: CommandSender) {
        sender.sendMessage("/deathpro - list all command")
    }

    @SubCommand(name = "help", permission = "deathpro.help")
    fun help(sender: CommandSender) {
        sender.sendMessage("/help - list all command")
    }

    @SubCommand(name = "spawncorpse", playerOnly = true, permission = "deathpro.spawncorpse")
    fun spawnCorpse(sender: CommandSender) {
        val player = sender as? Player ?: return
        corpseService.spawnCorpse(player)
    }

    @SubCommand(name = "reload", permission = "deathpro.reload")
    fun reload(sender: CommandSender) {
        p.reloadAllConfig()
        sender.sendMessage("${p.messageConfig.rawPrefix()} &areload config successfully".color())
    }
}