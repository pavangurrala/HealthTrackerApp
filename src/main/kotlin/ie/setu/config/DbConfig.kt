package ie.setu.config
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.postgresql.util.PSQLException

class DbConfig {
    private val logger = KotlinLogging.logger {}
    private lateinit var dbConfig: Database
    fun getDbConnection(): Database {
        val PGHOST = "dpg-cs770vggph6c73ff8j6g-a.frankfurt-postgres.render.com"
        val PGPORT = "5432"
        val PGUSER = "health_tracker_app_ekii_user"
        val PGPASSWORD = "2DxzI0sLHDAZcdjwIUqTsEUL4oAcupNR"
        val PGDATABASE = "health_tracker_app_ekii"

        val dbUrl = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"
        try {
            dbConfig = Database.connect(
                url = dbUrl, driver = "org.postgresql.Driver",
                user = PGUSER, password = PGPASSWORD
            )
            logger.info{"Connected to database..."+ dbConfig.url}
        } catch (ex: PSQLException) {
            logger.info{"Failed to connect to database...${ex.printStackTrace()}"}
        }
        return dbConfig
    }
}