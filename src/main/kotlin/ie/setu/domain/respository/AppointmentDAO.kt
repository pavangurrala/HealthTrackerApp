package ie.setu.domain.respository
import ie.setu.domain.db.AppointmentScheduler
import ie.setu.domain.models.AppointmentScheduling
import ie.setu.utils.mapToAppointmentScheduling
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
class AppointmentDAO {
    fun getAll(): ArrayList<AppointmentScheduling> {
        val appointmentList : ArrayList<AppointmentScheduling> = arrayListOf()
        transaction {
            AppointmentScheduler.selectAll().map { appointmentList.add(mapToAppointmentScheduling(it)) }
        }
        return appointmentList
    }

    fun getAppointmentById(id: Int): AppointmentScheduling? {
        return transaction {
            AppointmentScheduler.selectAll().where { AppointmentScheduler.id eq id }.map { mapToAppointmentScheduling(it) }.firstOrNull()
        }
    }
    fun getappointmentByUserId(userId: Int): List<AppointmentScheduling> {
        return transaction {
            AppointmentScheduler.selectAll().where{AppointmentScheduler.patientid eq userId}.map { mapToAppointmentScheduling(it) }
        }
    }
    fun save(appointmentScheduling: AppointmentScheduling) {
        transaction {
            AppointmentScheduler.insert {
                it[appointmentstartdatetime] = appointmentScheduling.appointmentstartdatetime
                it[appointmentendtime] = appointmentScheduling.appointmentendtime
                it[appointmenttype] = appointmentScheduling.appointmenttype
                it[locationofappointment] = appointmentScheduling.locationofappointment
                it[reasonforappointment] = appointmentScheduling.reasonforappointment
                it[appointmentstatus] = appointmentScheduling.appointmentstatus
                it[patientid] = appointmentScheduling.patientid
            }
        }
    }
    fun update(id: Int, appointmentScheduling: AppointmentScheduling) {
        return transaction {
            AppointmentScheduler.update({ AppointmentScheduler.id eq id }) {
                it[appointmentstartdatetime] = appointmentScheduling.appointmentstartdatetime
                it[appointmentendtime] = appointmentScheduling.appointmentendtime
                it[appointmenttype] = appointmentScheduling.appointmenttype
                it[locationofappointment] = appointmentScheduling.locationofappointment
                it[reasonforappointment] = appointmentScheduling.reasonforappointment
                it[appointmentstatus] = appointmentScheduling.appointmentstatus
                it[patientid] = appointmentScheduling.patientid
            }
        }
    }
    fun delete(id: Int) {
        return transaction {
            AppointmentScheduler.deleteWhere { AppointmentScheduler.id eq id }
        }
    }
    fun deleteappointmentbyuserid(userId: Int) {
        return transaction {
            AppointmentScheduler.deleteWhere { AppointmentScheduler.patientid eq userId }
        }
    }
}