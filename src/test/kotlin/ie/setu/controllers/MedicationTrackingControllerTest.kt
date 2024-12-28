package ie.setu.controllers
import ie.setu.config.DbConfig
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.MedicationTracking
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
class MedicationTrackingControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    val addedUser : User = jsonToObject(addUser(38, "James Matt", "jamesm@gmail.com", 85.2, 170.5, "Male",
        validdateofbirth).body.toString())
    @Nested
    inner class AddMedications{
        @Test
        fun `add an medication when a user exists for it, returns a 201 response`(){
            val addMedicationResponse = addMedication(
                medicationTracker[0].medicinename, medicationTracker[0].datetimeofintake, medicationTracker[0].dosage,
                medicationTracker[0].medtakenornot, addedUser.id
            )
            assertEquals(201, addMedicationResponse.status)
        }
    }
    @Nested
    inner class ReadMedication{
        @Test
        fun `get all medications from the database returns 200 or 404 response`(){
            val response = retrieveAllMedications()
            if (response.status == 200) {
                val retrievedMedications: ArrayList<MedicationTracking> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedMedications.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get all medications by user id when user and medications exists returns 200 response`(){
            addMedication(
                medicationTracker[0].medicinename, medicationTracker[0].datetimeofintake, medicationTracker[0].dosage,
                medicationTracker[0].medtakenornot, addedUser.id
            )
            addMedication(
                medicationTracker[1].medicinename, medicationTracker[1].datetimeofintake, medicationTracker[1].dosage,
                medicationTracker[1].medtakenornot, addedUser.id
            )
            val response = retrieveMedicationsByUserId(addedUser.id)
            assertEquals(201, response.status)
            val retrievedMedications = jsonNodeToObject<Array<MedicationTracking>>(response)
            assertEquals(3, retrievedMedications.size)
        }
        @Test
        fun `get all medications by user id when no medications exist returns 404 response`() {
            //Assert and Act - retrieve the activities by user id
            val response = retrieveMedicationsByUserId(-1)
            assertEquals(404, response.status)
        }
        @Test
        fun `get medications by medications id when no medications exists returns 404 response`() {
            //Arrange
            val medicationId = -1
            //Assert and Act - attempt to retrieve the activity by activity id
            val response = retrieveMedicationByMedicationId(medicationId)
            assertEquals(404, response.status)
        }
    }
    @Nested
    inner class UpdateMedication{
        @Test
        fun `updating an medications by medications id when it doesn't exist, returns a 404 response`(){
            val userId = -1
            val medicationID = -1
            assertEquals(404, retrieveMedicationsByUserId(userId).status)
            assertEquals(404, updateMedication(
                medicationID,medicationTracker[0].medicinename, medicationTracker[0].datetimeofintake, medicationTracker[0].dosage,
                medicationTracker[0].medtakenornot, addedUser.id
            ).status)
        }
        @Test
        fun `updating an medication by medication id when it exists, returns 200 response`(){
            val addMedicationResponse = addMedication(
                medicationTracker[0].medicinename, medicationTracker[0].datetimeofintake, medicationTracker[0].dosage,
                medicationTracker[0].medtakenornot, addedUser.id
            )
            assertEquals(201, addMedicationResponse.status)
            val newMedicationResponse: MedicationTracking = jsonToObject(addMedicationResponse.body.toString())
            val newMedicationID = newMedicationResponse.id
            val updatedMedicationResponse = updateMedication(newMedicationID, "abc", medicationTracker[0].datetimeofintake,
                medicationTracker[0].dosage,"taken", addedUser.id)
            assertEquals(200,updatedMedicationResponse.status)
            val retrievedMedicationResponse = retrieveMedicationByMedicationId(newMedicationID)
            val updatedMedication = jsonNodeToObject<MedicationTracking>(retrievedMedicationResponse)
            assertEquals("abc", updatedMedication.medicinename)
            assertEquals("taken", updatedMedication.medtakenornot)
        }
    }
    @Nested
    inner class DeleteMedication{
        @Test
        fun `deleting an medication by medication id when it doesn't exist, returns a 404 response`(){
            //Act & Assert - attempt to delete a Medication Record that doesn't exist
            assertEquals(404, deleteMedicationsByMedicationId(-1).status)
        }
        @Test
        fun `deleting medication by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteMedicationByUserId(-1).status)
        }
        @Test
        fun `deleting an medication by id when it exists, returns a 204 response`(){
            val addMedicationResponse = addMedication(
                medicationTracker[0].medicinename, medicationTracker[0].datetimeofintake, medicationTracker[0].dosage,
                medicationTracker[0].medtakenornot, addedUser.id
            )
            val addedMedication = jsonNodeToObject<MedicationTracking>(addMedicationResponse)
            assertEquals(204, deleteMedicationsByMedicationId(addedMedication.id).status)
        }
    }
    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    private fun addMedication(medicinename : String,datetimeofintake:DateTime,dosage: String,medtakenornot : String,patientid : Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/medicationtracker")
            .body("{\"medicinename\":\"$medicinename\", \"datetimeofintake\":\"$datetimeofintake\", \"dosage\":\"$dosage\", \"medtakenornot\":\"$medtakenornot\", \"patientid\":\"$patientid\"}")
            .asJson()
    }
    private fun updateMedication(id:Int, medicinename : String,datetimeofintake:DateTime,dosage: String,medtakenornot : String,patientid : Int): HttpResponse<JsonNode>{
        return Unirest.patch(origin + "/api/medicationtracker/$id")
            .body("{\"medicinename\":\"$medicinename\", \"datetimeofintake\":\"$datetimeofintake\", \"dosage\":\"$dosage\", \"medtakenornot\":\"$medtakenornot\", \"patientid\":\"$patientid\"}")
            .asJson()
    }
    //helper function to retrieve all medications
    private fun retrieveAllMedications(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/medicationtracker").asJson()
    }
    //helper function to retrieve medications by user id
    private fun retrieveMedicationsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/medicationtracker/$id").asJson()
    }
    //helper function to retrieve medications by medications id
    private fun retrieveMedicationByMedicationId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/medicationtracker/medicationrecord/$id").asJson()
    }
    //helper function to delete medications by user id
    private fun deleteMedicationByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/medicationtracker/medicationrecord/$id").asJson()
    }
    //helper function to delete medications by user id
    private fun deleteMedicationsByMedicationId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/medicationtracker/$id").asJson()
    }

}