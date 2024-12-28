package ie.setu.controllers
import ie.setu.config.DbConfig
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.LabReport
import ie.setu.domain.models.User
import ie.setu.helpers.*
import kong.unirest.core.HttpResponse
import kong.unirest.core.JsonNode
import kong.unirest.core.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LabReportControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    val addedUser : User = jsonToObject(addUser(39, "Jake Sullivan", "jakes@gmail.com", 85.2, 170.5, "Male",
        validdateofbirth).body.toString())
    @Nested
    inner class CreateLabReports{
        @Test
        fun `add an labreport when a user exists for it, returns a 201 response`(){
            val addLabReportResponse = addLabReports(
                labreport[0].reportname, labreport[0].reporttype, labreport[0].reportsource, addedUser.id
            )
            assertEquals(201,addLabReportResponse.status)
        }
    }
    @Nested
    inner class ReadLabReports{
        @Test
        fun `get all medications from the database returns 200 or 404 response`(){
            val response = retrieveAllLabReports()
            if (response.status == 200) {
                val retrievedLabReports: ArrayList<LabReport> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedLabReports.size)
            }else{
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get all labreports by user id when user and labreports exists returns 200 response`(){
            addLabReports(
                labreport[0].reportname, labreport[0].reporttype, labreport[0].reportsource, addedUser.id
            )
            addLabReports(
                labreport[1].reportname, labreport[1].reporttype, labreport[1].reportsource, addedUser.id
            )
            val response = retrieveLabReportsByUserId(addedUser.id)
            assertEquals(201, response.status)
            val retrievedLabReports: ArrayList<LabReport> = jsonToObject(response.body.toString())
            assertEquals(3, retrievedLabReports.size)
        }
        @Test
        fun `get all labreports by user id when no labreports exist returns 404 response`() {
            //Assert and Act - retrieve the labreport by user id
            val response = retrieveLabReportsByUserId(-1)
            assertEquals(404, response.status)
        }
        @Test
        fun `get labreports by labreport id when no labreports exists returns 404 response`() {
            //Arrange
            val labreportId = -1
            //Assert and Act - attempt to retrieve the labreport by labreport id
            val response = retrieveLabreportByLabreportId(labreportId)
            assertEquals(404, response.status)
        }
    }
    @Nested
    inner class UpdateLabReports{
        @Test
        fun `updating an labreports by labreport id when it doesn't exist, returns a 404 response`(){
            val userId = -1
            val labReportID = -1
            assertEquals(404, retrieveLabReportsByUserId(userId).status)
            assertEquals(404, updateLabReports(
                labReportID, labreport[0].reportname, labreport[0].reporttype, labreport[0].reportsource, addedUser.id
            ).status)
        }
        @Test
        fun `updating an labreport by labreport id when it exists, returns 200 response`(){
            val addLabReportResponse = addLabReports(
                labreport[0].reportname, labreport[0].reporttype, labreport[0].reportsource, addedUser.id
            )
            assertEquals(201,addLabReportResponse.status)
            val newLabReport:LabReport = jsonToObject(addLabReportResponse.body.toString())
            val newLabReportID = newLabReport.id
            val updateLabReportResponse = updateLabReports(newLabReportID, "Report_name_ct", "CTScan", labreport[0].reportsource, addedUser.id)
            assertEquals(200, updateLabReportResponse.status)
            val retrievedLabReportResponse = retrieveLabreportByLabreportId(newLabReportID)
            val updatedLabReport = jsonNodeToObject<LabReport>(retrievedLabReportResponse)
            assertEquals("Report_name_ct", updatedLabReport.reportname)
            assertEquals("CTScan", updatedLabReport.reporttype)
        }
    }
    @Nested
    inner class DeleteLabReports{
        @Test
        fun `deleting an labReport by labReport id when it doesn't exist, returns a 404 response`(){
            //Act & Assert - attempt to delete a labReport that doesn't exist
            assertEquals(404, deleteLabReportsByLabReportId(-1).status)
        }
        @Test
        fun `deleting labReport by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user labReport that doesn't exist
            assertEquals(400, deleteLabReportByUserId(-1).status)
        }
        @Test
        fun `deleting an labReport by id when it exists, returns a 204 response`(){
            val addLabReportResponse = addLabReports(
                labreport[0].reportname, labreport[0].reporttype, labreport[0].reportsource, addedUser.id
            )
            val addedLabReport = jsonNodeToObject<LabReport>(addLabReportResponse)
            assertEquals(204, deleteLabReportsByLabReportId(addedLabReport.id).status)
        }
    }
    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    //helper function to retrieve all labreports
    private fun retrieveAllLabReports(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/labreports").asJson()
    }
    //helper function to retrieve labreports by user id
    private fun retrieveLabReportsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/labreports/$id").asJson()
    }
    //helper function to retrieve labreport by labreport id
    private fun retrieveLabreportByLabreportId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/labreports/labreport/$id").asJson()
    }
    private fun addLabReports(reportname: String, reporttype: String, reportsource: String, patientid: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/labreports")
            .body("{\"reportname\":\"$reportname\", \"reporttype\":\"$reporttype\", \"reportsource\":\"$reportsource\",\"patientid\":\"$patientid\"}")
            .asJson()
    }
    private fun updateLabReports(id: Int, reportname: String, reporttype: String, reportsource: String, patientid: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/labreports/$id")
            .body("{\"reportname\":\"$reportname\", \"reporttype\":\"$reporttype\", \"reportsource\":\"$reportsource\",\"patientid\":\"$patientid\"}")
            .asJson()
    }
    //helper function to delete LabReport by user id
    private fun deleteLabReportByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/labreports/labreport/$id").asJson()
    }
    //helper function to delete LabReport by LabReport id
    private fun deleteLabReportsByLabReportId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/labreports/$id").asJson()
    }

}