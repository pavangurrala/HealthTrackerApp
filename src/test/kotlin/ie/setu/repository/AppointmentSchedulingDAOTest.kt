package ie.setu.repository
import ie.setu.domain.db.AppointmentScheduler
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import ie.setu.helpers.appointments
import ie.setu.domain.models.AppointmentScheduling
import org.junit.jupiter.api.Test
import ie.setu.domain.respository.AppointmentDAO
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.populateAppointmentTable
import org.junit.jupiter.api.Nested
import org.joda.time.DateTime
class AppointmentSchedulingDAOTest {
    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    private val appointment1 = appointments.get(0)
    private val appointment2 = appointments.get(1)
    private val appointment3 = appointments.get(2)

    @Nested
    inner class CreateAppointments {
        @Test
        fun `multiple appointments added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(3, appointmentDAO.getAll().size)
                assertEquals(appointment1, appointmentDAO.getAppointmentById(appointment1.id))
                assertEquals(appointment2, appointmentDAO.getAppointmentById(appointment2.id))
                assertEquals(appointment3, appointmentDAO.getAppointmentById(appointment3.id))
            }
        }
    }
    @Nested
    inner class ReadAppointments {
        @Test
        fun `getting all appointments from a populated table returns all rows`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(3, appointmentDAO.getAll().size)
            }
        }
        @Test
        fun `get appointments by user id that has no appointments, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(0, appointmentDAO.getappointmentByUserId(3).size)
            }
        }
        @Test
        fun `get appointment by user id that exists, results in a correct appointment(s) returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(appointment1, appointmentDAO.getappointmentByUserId(1).get(0))
                assertEquals(appointment2, appointmentDAO.getappointmentByUserId(1).get(1))
                assertEquals(appointment3, appointmentDAO.getappointmentByUserId(2).get(0))
            }
        }
        @Test
        fun `get all appointments over empty table returns none`(){
            transaction {
                //Arrange - create and setup appointmentDAO object
                SchemaUtils.create(AppointmentScheduler)
                val appointmentDAO = AppointmentDAO()
                //Act & Assert
                assertEquals(0, appointmentDAO.getAll().size)
            }
        }
        @Test
        fun `get appointment by appointment id that has no records, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(null, appointmentDAO.getAppointmentById(4))
            }
        }
        @Test
        fun `get appointment by appointment id that exists, results in a correct appointment returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                assertEquals(appointment1, appointmentDAO.getAppointmentById(appointment1.id))
                assertEquals(appointment3, appointmentDAO.getAppointmentById(appointment3.id))
            }
        }
    }
    @Nested
    inner class UpdateAppointments {
        @Test
        fun `updating existing appointment in table results in successful update`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                val updatedappointment = AppointmentScheduling(id=1, appointmentstartdatetime = DateTime.parse("2024-11-15T06:00:00.000"), appointmentendtime = DateTime.parse("2024-11-15T06:30:00.000"), appointmentstatus = "Cancelled", appointmenttype = "Online", locationofappointment = "Hospital", reasonforappointment = "Fever", patientid = 1)
                appointmentDAO.update(updatedappointment.id, updatedappointment)
                assertEquals(updatedappointment, appointmentDAO.getAppointmentById(1))

            }
        }
        @Test
        fun `updating non-existant appointment in table results in no updates`(){
            transaction {
                //Arrange - create and populate tables with three users and three appointments
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                //Act & Assert
                val updatedappointment5 = AppointmentScheduling(id=5, appointmentstartdatetime = DateTime.parse("2024-11-15T06:00:00.000"), appointmentendtime = DateTime.parse("2024-11-15T06:30:00.000"), appointmentstatus = "on hold", appointmenttype = "Online", locationofappointment = "Hospital", reasonforappointment = "Fever", patientid = 1)
                appointmentDAO.update(5, updatedappointment5)
                assertEquals(null, appointmentDAO.getAppointmentById(5))
                assertEquals(3, appointmentDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteAppointments {
        @Test
        fun `deleting an existing appointment (by appointment id) in table results in record being deleted`(){
            transaction {
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                assertEquals(3, appointmentDAO.getAll().size)
                appointmentDAO.delete(appointment2.id)
                assertEquals(2, appointmentDAO.getAll().size)
            }
        }
        @Test
        fun `deleting a non-existant appointment (by appointment id) in table results in no deletion`(){
            transaction {
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                assertEquals(3, appointmentDAO.getAll().size)
                appointmentDAO.delete(5)
                assertEquals(3, appointmentDAO.getAll().size)
            }
        }
        @Test
        fun `deleting appointments when 1 or more exist for user id results in deletion`(){
            transaction {
                val userDAO = populateUserTable()
                val appointmentDAO = populateAppointmentTable()
                assertEquals(3, appointmentDAO.getAll().size)
                appointmentDAO.deleteappointmentbyuserid(1)
                assertEquals(1, appointmentDAO.getAll().size)
            }
        }
    }
}