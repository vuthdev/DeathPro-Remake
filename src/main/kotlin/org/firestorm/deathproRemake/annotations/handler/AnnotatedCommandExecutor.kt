package org.firestorm.deathproRemake.annotations.handler

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.annotations.Args
import org.firestorm.deathproRemake.annotations.RegisterCommand
import org.firestorm.deathproRemake.annotations.SubCommand
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class AnnotatedCommandExecutor(
    private val handler: Any,
    private val meta: RegisterCommand,
) {
    // Build a map of subcommand-name → KFunction at construction time
    private val subCommands: Map<String, KFunction<*>> = handler::class.memberFunctions
        .filter { it.hasAnnotation<SubCommand>() }
        .associateBy { it.findAnnotation<SubCommand>()!!.name.lowercase() }

    fun dispatch(sender: CommandSender, args: Array<String>): Boolean {
        val subName = args.getOrNull(0)?.lowercase()
        val fn = when {
            // no args → use default handler
            subName == null -> subCommands[""]

            // known subcommand → use it
            subCommands.containsKey(subName) -> subCommands[subName]

            // unknown subcommand AND default exists → use default
            subCommands.containsKey("") -> subCommands[""]

            // unknown subcommand, no default → show usage
            else -> null
        }

        if (fn == null) {
            sender.sendMessage("§cUsage: /${meta.name} <${
                subCommands.keys.filter { it.isNotEmpty() }.joinToString("|")
            }>")
            return true
        }

        val sub = fn.findAnnotation<SubCommand>()!!

        // Permission guard
        if (sub.permission.isNotEmpty() && !sender.hasPermission(sub.permission)) {
            sender.sendMessage("§cYou don't have permission.")
            return true
        }

        // Player-only guard
        if (sub.playerOnly && sender !is Player) {
            sender.sendMessage("§cOnly players can use this command.")
            return true
        }

        // Build call arguments: first param is always the sender
        val callArgs: MutableList<Any?> = mutableListOf(handler, sender)

        for (param in fn.parameters.drop(2)) { // drop instanceParam + sender
            val argAnnotation = param.findAnnotation<Args>()
            if (argAnnotation != null) {
                val raw = args.getOrNull(argAnnotation.index + 1) // +1 because args[0] = subName
                    ?: if (argAnnotation.optional) argAnnotation.default else null

                if (raw == null) {
                    sender.sendMessage("§cMissing argument: <${argAnnotation.name}>")
                    return true
                }

                val coerced = coerce(raw, param.type) ?: run {
                    sender.sendMessage("§cInvalid value for <${argAnnotation.name}>: §7$raw")
                    return true
                }
                callArgs.add(coerced)
            } else {
                callArgs.add(null)
            }
        }

        fn.isAccessible = true
        fn.call(*callArgs.toTypedArray())
        return true
    }

    fun tabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return if (args.size <= 1) subCommands.keys.filter { it.startsWith(args.getOrElse(0) { "" }) }
        else emptyList()
    }

    private fun coerce(raw: String, type: KType): Any? = when (type.classifier) {
        String::class  -> raw
        Int::class     -> raw.toIntOrNull()
        Double::class  -> raw.toDoubleOrNull()
        Long::class    -> raw.toLongOrNull()
        Boolean::class -> raw.toBooleanStrictOrNull()
        Player::class  -> Bukkit.getPlayerExact(raw)
        else           -> null
    }
}