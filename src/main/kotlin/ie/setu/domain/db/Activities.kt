package ie.setu.domain.db
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import java.sql.Timestamp
object Activities : Table("activity") {
    val id = integer(name = "id").autoIncrement()
    val description = varchar("description", 255)
    val duration = double("duration")
    val calories = double("calories")
    val started = DateTime("started")
    val userid = integer("userid")
}