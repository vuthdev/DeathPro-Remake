package org.firestorm.deathproRemake.storage.database

import org.firestorm.deathproRemake.DeathproRemake
import org.firestorm.deathproRemake.storage.database.table.CorpseTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
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
        }
    }
}