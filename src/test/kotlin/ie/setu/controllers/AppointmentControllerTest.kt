package ie.setu.controllers
import ie.setu.config.DbConfig
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.AppointmentScheduling
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
class AppointmentControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    val addedUser : User = jsonToObject(addUser(37, "John Sullivan", "johns@gmail.com", 85.2, 170.5, "Male",
        validdateofbirth).body.toString())
    @Nested
    inner class createAppointments {
        @Test
        fun `add an activity when a user exists for it, returns a 201 response`(){
            val addAppointmentResponse = addAppointment(
                appointments[0].appointmentstartdatetime, appointments[0].appointmentendtime,appointments[0].appointmenttype,
                appointments[0].locationofappointment,appointments[0].reasonforappointment,appointments[0].appointmentstatus,
                addedUser.id
            )
            assertEquals(201, addAppointmentResponse.status)
            val newappointmentResponse: AppointmentScheduling = jsonToObject(addAppointmentResponse.body.toString())
            deleteAppointmentByAppointmentId(newappointmentResponse.id)
        }
    }
    @Nested
    inner class readAppointments {
        @Test
        fun `get all appointments from the database returns 200 or 404 response`(){
            val response = retrieveAllAppointments()
            if(response.status == 200){
                //val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                val retrievedAppointments: ArrayList<AppointmentScheduling> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedAppointments.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get all appointments by user id when user and appointments exists returns 200 response`(){
            addAppointment(
                appointments[0].appointmentstartdatetime, appointments[0].appointmentendtime,appointments[0].appointmenttype,
                appointments[0].locationofappointment,appointments[0].reasonforappointment,appointments[0].appointmentstatus,
                addedUser.id
            )
            addAppointment(
                appointments[1].appointmentstartdatetime, appointments[1].appointmentendtime,appointments[1].appointmenttype,
                appointments[1].locationofappointment,appointments[1].reasonforappointment,appointments[1].appointmentstatus,
                addedUser.id
            )
            val response = retrieveAppointmentsByUserId(addedUser.id)
            assertEquals(201, response.status)
            val retrievedAppointments = jsonNodeToObject<Array<AppointmentScheduling>>(response)
            assertEquals(2, retrievedAppointments.size)

        }
        @Test
        fun `get all appointments by user id when no appointments exist returns 404 response`() {
            //Assert and Act - retrieve the activities by user id
            val response = retrieveAppointmentsByUserId(-1)
            assertEquals(404, response.status)
        }
        @Test
        fun `get appointment by appointment id when no appointment exists returns 404 response`() {
            //Arrange
            val appointmentId = -1
            //Assert and Act - attempt to retrieve the activity by activity id
            val response = retrieveAppointmentByAppointmentId(appointmentId)
            assertEquals(404, response.status)
        }

    }
    @Nested
    inner class updateAppointments {
        @Test
        fun `updating an appointment by appointment id when it doesn't exist, returns a 404 response`(){
            val userId = -1
            val appointmentID = -1
            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveAppointmentsByUserId(userId).status)
            assertEquals(404, updateAppointment(
                appointmentID,appointments[1].appointmentstartdatetime, appointments[1].appointmentendtime,appointments[1].appointmenttype,
                appointments[1].locationofappointment,appointments[1].reasonforappointment,appointments[1].appointmentstatus,
                addedUser.id
            ).status)
        }
        @Test
        fun `updating an appointment by appointment id when it exists, returns 200 response`(){
            val addAppointmentResponse = addAppointment(
                appointments[0].appointmentstartdatetime, appointments[0].appointmentendtime,appointments[0].appointmenttype,
                appointments[0].locationofappointment,appointments[0].reasonforappointment,appointments[0].appointmentstatus,
                addedUser.id
            )
            assertEquals(201, addAppointmentResponse.status)
            val newappointmentResponse: AppointmentScheduling = jsonToObject(addAppointmentResponse.body.toString())
            val newappointmentID = newappointmentResponse.id
            val updatedAppointmentResponse = updateAppointment(newappointmentID, newappointmentResponse.appointmentstartdatetime, newappointmentResponse.appointmentendtime, "walkin", newappointmentResponse.locationofappointment, "cough", newappointmentResponse.appointmentstatus, addedUser.id)
            assertEquals(200, updatedAppointmentResponse.status)
            val retrievedAppointmentResponse = retrieveAppointmentByAppointmentId(newappointmentID)
            val updatedAppointment = jsonNodeToObject<AppointmentScheduling>(retrievedAppointmentResponse)
            assertEquals("walkin", updatedAppointment.appointmenttype)
            assertEquals("cough", updatedAppointment.reasonforappointment)
        }
    }
    @Nested
    inner class deleteAppointments {
        @Test
        fun `deleting an appointment by appointment id when it doesn't exist, returns a 404 response`(){
            //Act & Assert - attempt to delete a appointment that doesn't exist
            assertEquals(404, deleteAppointmentByAppointmentId(-1).status)
        }
        @Test
        fun `deleting appointment by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteAppointmentsByUserId(-1).status)
        }
        @Test
        fun `deleting an appointment by id when it exists, returns a 204 response`(){
            val addAppointmentResponse = addAppointment(
                appointments[0].appointmentstartdatetime, appointments[0].appointmentendtime,appointments[0].appointmenttype,
                appointments[0].locationofappointment,appointments[0].reasonforappointment,appointments[0].appointmentstatus,
                addedUser.id
            )
            val addeddAppointment = jsonNodeToObject<AppointmentScheduling>(addAppointmentResponse)
            assertEquals(204, deleteAppointmentByAppointmentId(addeddAppointment.id).status)

        }
    }

    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    //helper function to retrieve all appointments
    private fun retrieveAllAppointments(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/appointmentscheduler").asJson()
    }
    //helper function to add an appointment
    private fun addAppointment(appointmentstartdatetime: DateTime, appointmentendtime: DateTime, appointmenttype : String,
                               locationofappointment : String, reasonforappointment : String, appointmentstatus: String, patientid: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/appointmentscheduler")
        .body("{\"appointmentstartdatetime\":\"$appointmentstartdatetime\",\"appointmentendtime\":\"$appointmentendtime\", \"appointmenttype\":\"$appointmenttype\", \"locationofappointment\":\"$locationofappointment\", \"reasonforappointment\":\"$reasonforappointment\", \"appointmentstatus\":\"$appointmentstatus\",\"patientid\":\"$patientid\"}")
                    .asJson()
    }
    //helper function to delete an appointment by appointment id
    private fun deleteAppointmentByAppointmentId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/appointmentscheduler/$id").asString()
    }
    //helper function to retrieve appointments by user id
    private fun retrieveAppointmentsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/appointmentscheduler/$id").asJson()
    }
    //helper function to retrieve appointment by appointment id
    private fun retrieveAppointmentByAppointmentId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/appointmentscheduler/appointment/$id").asJson()
    }
    //helper function to retrieve appointments by user id
    private fun deleteAppointmentsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/appointmentscheduler/appointment/$id").asJson()
    }
    //helper function to add an appointment
    private fun updateAppointment(id:Int,appointmentstartdatetime: DateTime, appointmentendtime: DateTime, appointmenttype : String,
                               locationofappointment : String, reasonforappointment : String, appointmentstatus: String, patientid: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/appointmentscheduler/$id")
            .body("{\"appointmentstartdatetime\":\"$appointmentstartdatetime\",\"appointmentendtime\":\"$appointmentendtime\", \"appointmenttype\":\"$appointmenttype\", \"locationofappointment\":\"$locationofappointment\", \"reasonforappointment\":\"$reasonforappointment\", \"appointmentstatus\":\"$appointmentstatus\",\"patientid\":\"$patientid\"}")
            .asJson()
    }
}