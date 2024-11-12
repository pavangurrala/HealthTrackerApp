package ie.setu.domain.db
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
object Activities : Table("activity") {
    val id = integer(name = "id").autoIncrement()
    val description = varchar("description", 255)
    val duration = double("duration")
    val calories = double("calories")
    val started = datetime("started")
    val userid = integer("userid").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(Activities.id, name = "PK_Activities_ID")
}