package org.firestorm.deathproRemake.annotations.handler

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.java.JavaPlugin
import org.firestorm.deathproRemake.annotations.RegisterCommand
import kotlin.reflect.full.findAnnotation

object CommandRegistry {

    fun scan(plugin: JavaPlugin, vararg instances: Any) {
        val commandMap = Bukkit.getServer()
            .javaClass.getDeclaredField("commandMap")
            .also { it.isAccessible = true }
            .get(Bukkit.getServer()) as SimpleCommandMap

        for (instance in instances) {
            val clazz = instance::class
            val meta  = clazz.findAnnotation<RegisterCommand>() ?: continue
            val executor = AnnotatedCommandExecutor(instance, meta)

            val command = object : Command(meta.name) {
                override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean =
                    executor.dispatch(sender, args)
                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> =
                    executor.tabComplete(sender, args)
            }.apply {
                description = meta.description
                usage       = meta.usage.ifEmpty { "/${meta.name}" }
                aliases     = meta.aliases.toMutableList()
                if (meta.permission.isNotEmpty()) permission = meta.permission
            }

            commandMap.register(plugin.name.lowercase(), command)
        }
    }
}