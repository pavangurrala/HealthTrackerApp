package ie.setu.domain.db
import ie.setu.domain.db.AppointmentScheduler.references
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
object MedicationTracker : Table("MedicationTracker") {
    val id = integer("id").autoIncrement()
    val medicinename = varchar("medicinename", 50)
    val datetimeofintake = datetime("datetimeofintake")
    val dosage = varchar("dosage", 20)
    val medtakenornot = varchar("medtakenornot",50)
    val patientid = integer("patientid").references(Users.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(MedicationTracker.id, name="PK_MedicationTracker_ID")
}