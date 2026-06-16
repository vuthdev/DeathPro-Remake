package org.firestorm.deathproRemake.common.constants

object DefaultSkins {
    // Official Mojang Steve Skin URL wrapped into the proper Minecraft property JSON format
    // Value: {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/3b60a1f0fb63eab29c334515d9baec12c3efe92c56fd0a101bb4555e3b8e108"}}}
    const val STEVE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I2MGExZjBmYjYzZWFiMjljMzM0NTE1ZDliYWVjMTJjM2VmZTkyYzU2ZmQwYTEwMWJiNDU1NWUzYjhlMTA4In19fQ=="

    // Default skins do not require a cryptographic signature from the client side
    val STEVE_SIGNATURE: String? = null
}