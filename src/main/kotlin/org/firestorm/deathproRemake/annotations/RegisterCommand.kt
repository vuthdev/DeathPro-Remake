package org.firestorm.deathproRemake.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterCommand(
    val name: String,
    val description: String = "",
    val usage: String = "",
    val aliases: Array<String> = [],
    val permission: String = "",
)