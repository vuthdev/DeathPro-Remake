package org.firestorm.deathproRemake.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommand(
    val name: String = "",
    val description: String = "",
    val permission: String = "",
    val playerOnly: Boolean = false,
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Args(
    val index: Int,
    val name: String = "arg",
    val optional: Boolean = false,
    val default: String = "",
)