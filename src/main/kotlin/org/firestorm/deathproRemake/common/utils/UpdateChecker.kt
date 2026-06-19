package org.firestorm.deathproRemake.common.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.common.constants.BaseConstants
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.color
import java.net.URI
import java.net.URL

class UpdateChecker(
    private val plugin: DeathproRemake,
    private val githubUser: String,
    private val githubRepo: String,
) {
    private val currentVersion = plugin.description.version
    private var latestVersion: String? = null
    private var updateAvailable = false

    fun check() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val url = URI.create(
                    "https://api.github.com/repos/$githubUser/$githubRepo/releases/latest"
                ).toURL()
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("User-Agent", "DeathPro-UpdateChecker")
                }

                if (connection.responseCode != 200) {
                    clogger.warn("&cFailed to check for updates: HTTP ${connection.responseCode}".color())
                    return@Runnable
                }

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val tagName = response
                    .substringAfter("\"tag_name\":\"")
                    .substringBefore("\"")
                    .removePrefix("v")

                latestVersion  = tagName
                updateAvailable = isNewerVersion(tagName, currentVersion)

                if (updateAvailable) {
                    clogger.warn("&eUpdate available&f: &c$currentVersion → &a$tagName".color())
                    clogger.warn("&eDownload: &fhttps://github.com/$githubUser/$githubRepo/releases/latest".color())
                } else {
                    clogger.info("&aPlugin is up to date ($currentVersion)".color())
                }

            } catch (e: Exception) {
                clogger.warn("&cCould not check for updates: ${e.message}".color())
            }
        })
    }

    fun notifyPlayer(player: Player) {
        if (!updateAvailable) return
        if (!player.hasPermission(BaseConstants.ADMIN_PERMISSION)) return

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.sendMessage(
                "${plugin.messageConfig.rawPrefix()}&eUpdate available! &f$currentVersion &e→ &f$latestVersion".color()
            )
            player.sendMessage(
                "${plugin.messageConfig.rawPrefix()}$7Download: &fhttps://github.com/$githubUser/$githubRepo/releases/latest".color()
            )
        }, 40L)
    }

    // compare semantic versions: "1.0.1" > "1.0.0"
    private fun isNewerVersion(latest: String, current: String): Boolean {
        return try {
            val latestParts  = latest.split(".").map { it.toIntOrNull() ?: 0 }
            val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val size = maxOf(latestParts.size, currentParts.size)

            for (i in 0 until size) {
                val l = latestParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }
}