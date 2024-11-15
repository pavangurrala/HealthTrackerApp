package ie.setu.repository
import ie.setu.domain.db.Labreports
import ie.setu.domain.models.LabReport
import ie.setu.domain.respository.LabReportDAO
import ie.setu.helpers.populateLabReportTable
import ie.setu.helpers.labreport
import ie.setu.helpers.populateUserTable
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.Nested
import org.joda.time.DateTime
class LabReportsDAOTest {
    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    private val labreport1 = labreport.get(0)
    private val labreport2 = labreport.get(1)
    private val labreport3 = labreport.get(2)

    @Nested
    inner class CreateLabReports {
        @Test
        fun `multiple labreports added to table can be retrieved successfully`(){
            transaction {
                //Arrange - create and populate tables with three users and three labreports
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                //Act & Assert
                assertEquals(3, labreportDAO.getAll().size)
                assertEquals(labreport1, labreportDAO.getLabReportsByID(labreport1.id))
                assertEquals(labreport2, labreportDAO.getLabReportsByID(labreport2.id))
                assertEquals(labreport3, labreportDAO.getLabReportsByID(labreport3.id))
            }
        }
    }
    @Nested
    inner class ReadLabReports {
        @Test
        fun `getting all labreports from a populated table returns all rows`(){
            transaction {
                //Arrange - create and populate tables with three users and three labreports
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                //Act & Assert
                assertEquals(3, labreportDAO.getAll().size)
            }
        }
        @Test
        fun `get labreport by user id who does not have any labreports, results in no record returned`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                assertEquals(0, labreportDAO.getLabReportsByUserID(3).size)
            }
        }
        @Test
        fun `get labreport by user id that exists, results in a correct labreport(s) returned`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()

                assertEquals(labreport1, labreportDAO.getLabReportsByUserID(1).get(0))
                assertEquals(labreport2, labreportDAO.getLabReportsByUserID(1).get(1))
                assertEquals(labreport3, labreportDAO.getLabReportsByUserID(2).get(0))
            }
        }
        @Test
        fun `get all labreports over empty table returns none`(){
            transaction {
                SchemaUtils.create(Labreports)
                val labreportDAO = LabReportDAO()

                assertEquals(0, labreportDAO.getAll().size)
            }
        }
        @Test
        fun `get labreports by labreport id that has no records, results in no record returned`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()

                assertEquals(null, labreportDAO.getLabReportsByID(6))
            }
        }
        @Test
        fun `get labreport by labreport id that exists, results in a correct activity returned`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                assertEquals(labreport1, labreportDAO.getLabReportsByID(1))
                assertEquals(labreport2, labreportDAO.getLabReportsByID(2))
            }
        }
    }
    @Nested
    inner class UpdateLabReports {
        @Test
        fun `updating existing labreport in table results in successful update`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()

                val updatedlabreport = LabReport(id=2,reportname = "Report_name_xr", reporttype = "X-Ray", reportsource = "ANC_Hospital", patientid = 1)
                labreportDAO.updateLabReport(updatedlabreport.id,updatedlabreport)
                assertEquals(updatedlabreport, labreportDAO.getLabReportsByID(2))
            }
        }
        @Test
        fun `updating non-existant labreport in table results in no updates`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                val updatedlabreport = LabReport(id=5,reportname = "Report_name_xr", reporttype = "X-Ray", reportsource = "ANC_Hospital", patientid = 1)
                labreportDAO.updateLabReport(5,updatedlabreport)
                assertEquals(null, labreportDAO.getLabReportsByID(5))
                assertEquals(3, labreportDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteLabReports {
        @Test
        fun `deleting existing labreport in table results in successful delete`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                assertEquals(3, labreportDAO.getAll().size)
                labreportDAO.deleteLabReport(labreport1.id)
                assertEquals(2, labreportDAO.getAll().size)
            }
        }
        @Test
        fun `deleting non-existent labreport for user id in table results in no deletes`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()

                assertEquals(3, labreportDAO.getAll().size)
                labreportDAO.getLabReportsByUserID(3)
                assertEquals(3, labreportDAO.getAll().size)
            }
        }
        @Test
        fun `deleting labreport by user id that has 1 or more labreports`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                assertEquals(3, labreportDAO.getAll().size)
                labreportDAO.deleteLabReportByUserId(labreport1.id)
                assertEquals(1, labreportDAO.getAll().size)
            }
        }
        @Test
        fun `deleting a non-existant labreport (by labreport id) in table results in no deletion`(){
            transaction {
                val userDAO = populateUserTable()
                val labreportDAO = populateLabReportTable()
                assertEquals(3, labreportDAO.getAll().size)
                labreportDAO.deleteLabReport(5)
                assertEquals(3, labreportDAO.getAll().size)
            }
        }
    }
}