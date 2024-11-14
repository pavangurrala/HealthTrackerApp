package ie.setu.domain.db
import ie.setu.domain.db.Labreports.autoIncrement
import ie.setu.domain.db.Labreports.references
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Labreports : Table("labreports"){
    val id = integer(name = "id").autoIncrement()
    val reportname = varchar("reportname", 100)
    val reporttype = varchar("reporttype", 100)
    val reportsource = varchar("reportsource", 100)
    val patientid = integer(name = "patientid").references(Users.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(Labreports.id, name = "PK_LABREPORT_ID")
}