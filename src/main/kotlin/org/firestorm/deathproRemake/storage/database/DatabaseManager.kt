package org.firestorm.deathproRemake.storage.database

import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.common.extension.clogger
import org.firestorm.deathproRemake.common.extension.color
import org.firestorm.deathproRemake.storage.database.table.CorpseTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import java.sql.Connection

class DatabaseManager(private val plugin: DeathproRemake) {
    lateinit var database: Database
        private set

    fun connect() {
        val dbFile = File(plugin.dataFolder, "deathpro.db")

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        database = Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC",
        )

        transaction(
            transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
            db = database,
        ) {
            SchemaUtils.create(
                CorpseTable,
                // add more tables here later
            )

            val conn = TransactionManager.current().connection
            val rawJdbcConn = conn.connection as Connection

            var hasOldColumn = false
            var hasNewColumn = false

            try {
                val dbMetaData = rawJdbcConn.metaData

                val columnsResultSet = dbMetaData.getColumns(null, null, "corpses", null)

                while(columnsResultSet.next()) {
                    val columnName = columnsResultSet.getString("COLUMN_NAME")
                    if (columnName.equals("expired_at", ignoreCase = true)) {
                        hasOldColumn = true
                    }
                    if (columnName.equals("remaining_seconds", ignoreCase = true)) {
                        hasNewColumn = true
                    }
                }
                columnsResultSet.close()

            } catch (e: Exception) {
                clogger.warn("&6Could not read database metadata: ${e.message}".color())
            }

            if (hasOldColumn && !hasNewColumn) {
                clogger.info("&aDatabase schema update required. Migrating structural changes...".color())

                if (!hasNewColumn) {
                    try {
                        rawJdbcConn.prepareStatement("ALTER TABLE corpses ADD COLUMN remaining_seconds BIGINT DEFAULT 0;").executeUpdate()
                        clogger.info("&aSuccessfully added missing column 'remaining_seconds'.".color())
                    } catch (e: Exception) {
                        // Fallback fallback safe handler
                        clogger.warn("&6Error trying to add new column ${e.message}")
                    }
                }

                if (hasOldColumn) {
                    try {
                        rawJdbcConn.prepareStatement("ALTER TABLE corpses DROP COLUMN expired_at;").executeUpdate()
                        clogger.info("&aSuccessfully cleaned up legacy column 'expired_at'.".color())
                    } catch (e: Exception) {
                        // Fallback fallback safe handler
                        clogger.warn("&6Error trying to clean up old column ${e.message}")
                    }
                }

                clogger.info("&aDatabase migration finished successfully!".color())
            } else {
                clogger.info("&aDatabase is up to date. No migration needed.".color())
            }
        }
    }
}