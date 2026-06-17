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
        sendHelpMenu(sender)
    }

    @SubCommand(name = "help", permission = "deathpro.help")
    fun help(sender: CommandSender) {
        sendHelpMenu(sender)
    }

    @SubCommand(name = "spawncorpse", playerOnly = true, permission = "deathpro.spawncorpse")
    fun spawnCorpse(sender: CommandSender) {
        val player = sender as? Player ?: return
        player.sendMessage("${p.messageConfig.rawPrefix()}spawned successfully!")
        corpseService.spawnCorpse(player)
    }

    @SubCommand(name = "reload", permission = "deathpro.reload")
    fun reload(sender: CommandSender) {
        p.reloadAllConfig()
        sender.sendMessage("${p.messageConfig.rawPrefix()}&areload config successfully".color())
    }

    fun sendHelpMenu(sender: CommandSender) {
        sender.sendMessage("&8&l&m---------------------------------------------".color())
        sender.sendMessage("&c&lDeathPro &7- Command Help Menu".color())
        sender.sendMessage("&8&l&m---------------------------------------------".color())
        sender.sendMessage("&7» &c/deathpro &8- &7main command.".color())
        sender.sendMessage("&7» &c/deathpro help &8- &7Show this help menu.".color())
        sender.sendMessage("&7» &c/deathpro reload &8- &7Reload the config.yml safely.".color())
        sender.sendMessage("&7» &c/deathpro spawncorpse &8- &7Test-spawn a corpse at your spot.".color())
        sender.sendMessage("&8&l&m---------------------------------------------".color())
    }
}