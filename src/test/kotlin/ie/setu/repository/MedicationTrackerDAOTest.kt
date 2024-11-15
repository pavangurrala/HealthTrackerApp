package ie.setu.repository
import ie.setu.domain.db.MedicationTracker
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import ie.setu.helpers.medicationTracker
import ie.setu.domain.models.MedicationTracking
import org.junit.jupiter.api.Test
import ie.setu.domain.respository.MedicationTrackerDAO
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.populateMedicationTable
import org.junit.jupiter.api.Nested
import org.joda.time.DateTime

class MedicationTrackerDAOTest {
    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    private val medicationtracker1 = medicationTracker.get(0)
    private val medicationtracker2 = medicationTracker.get(1)
    private val medicationtracker3 = medicationTracker.get(2)

    @Nested
    inner class CreateMedications {
        @Test
        fun `multiple medication records added to table can be retrieved successfully`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(3, medicationDAO.getAll().size)
                assertEquals(medicationtracker1, medicationDAO.getMedicationTrackerById(medicationtracker1.id))
                assertEquals(medicationtracker2, medicationDAO.getMedicationTrackerById(medicationtracker2.id))
                assertEquals(medicationtracker3, medicationDAO.getMedicationTrackerById(medicationtracker3.id))
            }
        }
    }
    @Nested
    inner class ReadMedications {
        @Test
        fun `getting all medication records from a populated table returns all rows`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(3, medicationDAO.getAll().size)
            }
        }
        @Test
        fun `get medication record by user id that has no medication records, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(0, medicationDAO.getMedicationTrackerByUserID(4).size)
            }
        }
        @Test
        fun `get medication records by user id that exists, results in a correct medication record(s) returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(medicationtracker1, medicationDAO.getMedicationTrackerByUserID(1)[0])
                assertEquals(medicationtracker2, medicationDAO.getMedicationTrackerByUserID(1)[1])
                assertEquals(medicationtracker3, medicationDAO.getMedicationTrackerByUserID(2)[0])
            }
        }
        @Test
        fun `get all activities over empty table returns none`(){
            transaction {
                //Arrange - create and setup MedicationTrackerDAO object
                SchemaUtils.create(MedicationTracker)
                val medicationDAO = MedicationTrackerDAO()
                //Act & Assert
                assertEquals(0, medicationDAO.getAll().size)
            }
        }
        @Test
        fun `get medication record by medication tracker id that has no records, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(null, medicationDAO.getMedicationTrackerById(4))
            }
        }
        @Test
        fun `get medication record by medication tracker id that exists, results in a correct activity returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(medicationtracker1, medicationDAO.getMedicationTrackerById(1))
                assertEquals(medicationtracker3, medicationDAO.getMedicationTrackerById(3))
            }
        }
    }
    @Nested
    inner class UpdateMedicationRecords {
        @Test
        fun `updating existing medication record in table results in successful update`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                val updatedmedicationrecord = MedicationTracking(id=1,medicinename = "Dolo", datetimeofintake = DateTime.parse("2024-11-15T14:30:00.000"), dosage = "1-tab-500mg", medtakenornot = "1Tab in the morning", patientid = 1)
                medicationDAO.updateMedicationTracking(updatedmedicationrecord.id, updatedmedicationrecord)
                assertEquals(updatedmedicationrecord, medicationDAO.getMedicationTrackerById(1))
            }
        }
        @Test
        fun `updating non-existant medication record in table results in no updates`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                val updatedmedicationrecord = MedicationTracking(id=5,medicinename = "Dolo", datetimeofintake = DateTime.parse("2024-11-15T14:30:00.000"), dosage = "1-tab-500mg", medtakenornot = "1Tab in the morning", patientid = 1)
                medicationDAO.updateMedicationTracking(5, updatedmedicationrecord)
                assertEquals(null, medicationDAO.getMedicationTrackerById(5))
                assertEquals(3, medicationDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteMedicationRecords {
        @Test
        fun `deleting an existing medication record (by medication tracker id) in table results in record being deleted`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(3, medicationDAO.getAll().size)
                medicationDAO.deleteMedicationTracking(medicationtracker1.id)
                assertEquals(2, medicationDAO.getAll().size)
            }
        }
        @Test
        fun `deleting a non-existant medication record (by medication tracker id) in table results in no deletion`(){
            transaction {
                //Arrange - create and populate tables with three users and three medication records
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                //Act & Assert
                assertEquals(3, medicationDAO.getAll().size)
                medicationDAO.deleteMedicationTracking(5)
                assertEquals(3, medicationDAO.getAll().size)
            }
        }
        @Test
        fun `deleting medication records when 1 or more exist for user id results in deletion`(){
            transaction {
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                assertEquals(3, medicationDAO.getAll().size)
                medicationDAO.deleteMedicationTrackingByUserID(1)
                assertEquals(1, medicationDAO.getAll().size)
            }
        }
        @Test
        fun `deleting medication records when none exist for user id results in no deletion`(){
            transaction {
                val userDAO = populateUserTable()
                val medicationDAO = populateMedicationTable()
                assertEquals(3, medicationDAO.getAll().size)
                medicationDAO.deleteMedicationTrackingByUserID(3)
                assertEquals(3, medicationDAO.getAll().size)
            }
        }
    }
}