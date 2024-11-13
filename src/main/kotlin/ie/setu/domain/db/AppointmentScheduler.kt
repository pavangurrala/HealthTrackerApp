package ie.setu.domain.db
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
object AppointmentScheduler : Table("appointmentscheduler") {
    val id = integer("id").autoIncrement()
    val appointmentstartdatetime = datetime("appointmentstartdatetime")
    val appointmentendtime = datetime("appointmentendtime")
    val appointmenttype = varchar("appointmenttype", 255)
    val locationofappointment = varchar("locationofappointment", 255)
    val reasonforappointment = varchar("reasonforappointment", 255)
    val appointmentstatus = varchar("appointmentstatus", 255)
    val patientid = integer("patientid").references(Users.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(AppointmentScheduler.id, name="PK_Appointment_ID")
}