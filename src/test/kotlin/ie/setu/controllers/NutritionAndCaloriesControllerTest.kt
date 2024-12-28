package ie.setu.controllers
import ie.setu.config.DbConfig
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.NutritionandCalories
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
class NutritionAndCaloriesControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    val addedUser : User = jsonToObject(addUser(40, "Clementine", "clem@gmail.com", 65.2, 160.5, "FeMale",
        validdateofbirth).body.toString())
    @Nested
    inner class CreateNutritionAndCalories {
        @Test
        fun `add an NutritionAndCalories when a user exists for it, returns a 201 response`(){
            val addNCResponse = addNCRecord(
                nutritionandcalories[0].foodtaken,nutritionandcalories[0].caloriecount,nutritionandcalories[0].foodtakentime,
                nutritionandcalories[0].waterintake,nutritionandcalories[0].nutrients,addedUser.id,
            )
            assertEquals(201, addNCResponse.status)
        }

    }
    @Nested
    inner class ReadNutritionAndCalories {
        @Test
        fun `get all nutrition and calories records from the database returns 200 or 404 response`(){
            val response = retrieveAllNCRecords()
            if(response.status == 200){
                val retrievedNCRecords : ArrayList<NutritionandCalories> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedNCRecords.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get all NCRecords by user id when user and NCRecords exists returns 200 response`(){
            addNCRecord(
                nutritionandcalories[0].foodtaken,nutritionandcalories[0].caloriecount,nutritionandcalories[0].foodtakentime,
                nutritionandcalories[0].waterintake,nutritionandcalories[0].nutrients,addedUser.id,
            )
            addNCRecord(
                nutritionandcalories[1].foodtaken,nutritionandcalories[1].caloriecount,nutritionandcalories[1].foodtakentime,
                nutritionandcalories[1].waterintake,nutritionandcalories[1].nutrients,addedUser.id,
            )
            val response = retrieveNCRecordsByUserId(addedUser.id)
            assertEquals(201, response.status)
            val retrievedNCRecords : ArrayList<NutritionandCalories> = jsonToObject(response.body.toString())
            assertEquals(3, retrievedNCRecords.size)
        }
        @Test
        fun `get all NCRecords by user id when no NCRecords exist returns 404 response`() {
            //Assert and Act - retrieve the NCRecords by user id
            val response = retrieveNCRecordsByUserId(-1)
            assertEquals(404, response.status)
        }
        @Test
        fun `get NCRecords by NCRecord id when no NCRecords exists returns 404 response`() {
            //Arrange
            val NCRecordId = -1
            //Assert and Act - attempt to retrieve the NCRecord by NCRecord Id
            val response = retrieveNCRecordByNCRecordId(NCRecordId)
            assertEquals(404, response.status)
        }

    }
    @Nested
    inner class UpdateNutritionAndCalories {
        @Test
        fun `updating an NCRecords by NCRecord id when it doesn't exist, returns a 404 response`(){
            val userId = -1
            val NCRecordID = -1
            assertEquals(404, retrieveNCRecordsByUserId(userId).status)
            assertEquals(404, updateNCRecord(
                NCRecordID, nutritionandcalories[1].foodtaken,nutritionandcalories[1].caloriecount,nutritionandcalories[1].foodtakentime,
                nutritionandcalories[1].waterintake,nutritionandcalories[1].nutrients,addedUser.id,
            ).status)
        }
        @Test
        fun `updating an NCRecord by NCRecord id when it exists, returns 200 response`(){
            val addNCResponse = addNCRecord(
                nutritionandcalories[0].foodtaken,nutritionandcalories[0].caloriecount,nutritionandcalories[0].foodtakentime,
                nutritionandcalories[0].waterintake,nutritionandcalories[0].nutrients,addedUser.id,
            )
            assertEquals(201, addNCResponse.status)
            val newNCRecord : NutritionandCalories = jsonToObject(addNCResponse.body.toString())
            val newNCRecordID = newNCRecord.id
            val updatedNCRecordResponse = updateNCRecord(newNCRecordID,"Noodles", 100.2, nutritionandcalories[0].foodtakentime,
                nutritionandcalories[0].waterintake,nutritionandcalories[0].nutrients,addedUser.id,)
            assertEquals(204, updatedNCRecordResponse.status)
            val retrievedNCRecordResponse = retrieveNCRecordByNCRecordId(newNCRecordID)
            val updatedNCRecord = jsonNodeToObject<NutritionandCalories>(retrievedNCRecordResponse)
            assertEquals("Noodles", updatedNCRecord.foodtaken)
            assertEquals(100.2, updatedNCRecord.caloriecount)
        }
    }
    @Nested
    inner class DeleteNutritionAndCalories {
        @Test
        fun `deleting an NCRecord by NCRecord id when it doesn't exist, returns a 404 response`(){
            //Act & Assert - attempt to delete a NCRecord that doesn't exist
            assertEquals(404, deleteNCRecorsByNCRecorId(-1).status)
        }
        @Test
        fun `deleting NCRecord by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user  NCRecord that doesn't exist
            assertEquals(404, deleteNCRecordByUserId(-1).status)
        }
        @Test
        fun `deleting an labReport by id when it exists, returns a 204 response`(){
            val newNCRecord = addNCRecord(
                nutritionandcalories[0].foodtaken,nutritionandcalories[0].caloriecount,nutritionandcalories[0].foodtakentime,
                nutritionandcalories[0].waterintake,nutritionandcalories[0].nutrients,addedUser.id,
            )
            val addedNCRecord = jsonNodeToObject<NutritionandCalories>(newNCRecord)
            assertEquals(204, deleteNCRecorsByNCRecorId(addedNCRecord.id).status)
        }
    }


    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    //helper function to retrieve all NCRecords
    private fun retrieveAllNCRecords(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/nutritionandcalories").asJson()
    }
    //helper function to retrieve NCRecords by user id
    private fun retrieveNCRecordsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/nutritionandcalories/$id").asJson()
    }
    //helper function to retrieve NCRecord by NCRecord id
    private fun retrieveNCRecordByNCRecordId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/nutritionandcalories/nc/$id").asJson()
    }
    private fun addNCRecord(foodtaken: String, caloriecount: Double, foodtakentime: DateTime, waterintake: Double, nutrients: Double,userid: Int):HttpResponse<JsonNode>{
        return Unirest.post(origin + "/api/nutritionandcalories")
            .body("{\"foodtaken\":\"$foodtaken\",\"caloriecount\":\"$caloriecount\", \"foodtakentime\":\"$foodtakentime\", \"waterintake\":\"$waterintake\", \"nutrients\":\"$nutrients\", \"userid\":\"$userid\"}")
            .asJson()
    }
    private fun updateNCRecord(id:Int, foodtaken: String, caloriecount: Double, foodtakentime: DateTime, waterintake: Double, nutrients: Double,userid: Int):HttpResponse<JsonNode>{
        return Unirest.patch(origin + "/api/nutritionandcalories/$id")
            .body("{\"foodtaken\":\"$foodtaken\",\"caloriecount\":\"$caloriecount\", \"foodtakentime\":\"$foodtakentime\", \"waterintake\":\"$waterintake\", \"nutrients\":\"$nutrients\", \"userid\":\"$userid\"}")
            .asJson()
    }
    //helper function to delete NCRecord by user id
    private fun deleteNCRecordByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/nutritionandcalories/nc/$id").asJson()
    }
    //helper function to delete NCRecord by NCRecord id
    private fun deleteNCRecorsByNCRecorId(id: Int): HttpResponse<JsonNode> {
        return Unirest.delete(origin + "/api/nutritionandcalories/$id").asJson()
    }
}