package ie.setu.controllers
import ie.setu.config.DbConfig
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.User
import ie.setu.domain.models.Activity
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
class ActivitiesControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    val addedUser : User = jsonToObject(addUser(35, "John Sullivan", "johns@gmail.com", 85.2, 170.5, "Male",
        validdateofbirth).body.toString())
    @Nested
    inner class ReadActivities{
        @Test
        fun `get all activities from the database returns 200 or 404 response`(){
            val response = retrieveAllActivities()
            if(response.status == 200){
                //val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                val retrievedActivities: ArrayList<Activity> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedActivities.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get all activities by user id when user and activities exists returns 200 response`(){

            addActivity(
                activities[0].description, activities[0].duration, activities[0].calories, activities[0].started,
                addedUser.id
            )
            addActivity(
                activities[1].description, activities[1].duration, activities[1].calories, activities[1].started,
                addedUser.id
            )
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
            assertEquals(3, retrievedActivities.size)

        }
        @Test
        fun `get all activities by user id when no activities exist returns 404 response`() {
            //Assert and Act - retrieve the activities by user id
            val response = retrieveActivitiesByUserId(12)
            assertEquals(404, response.status)
        }
        @Test
        fun `get activity by activity id when no activity exists returns 404 response`() {
            //Arrange
            val activityId = -1
            //Assert and Act - attempt to retrieve the activity by activity id
            val response = retrieveActivityByActivityId(activityId)
            assertEquals(404, response.status)
        }
    }
    @Nested
    inner class CreateActivities{
        @Test
        fun `add an activity when a user exists for it, returns a 201 response`(){
            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id
            )
            assertEquals(201, addActivityResponse.status)
        }
    }
    @Nested
    inner class UpdateActivities{
        @Test
        fun `updating an activity by activity id when it doesn't exist, returns a 404 response`(){
            val userId = -1
            val activityID = -1
            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)
            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            assertEquals(
                404, updateActivity(
                    activityID, "swimming", 23.5, 180.5, DateTime.parse("2020-06-11T05:59:27.258Z")
                    , userId
                ).status
            )
        }
        @Test
        fun `updating an activity by activity id when it exists, returns 204 response`(){
            val addActivityResponse = addActivity(
                activities[0].description,
                activities[0].duration, activities[0].calories,
                activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse.status)
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)
            val activityId = addedActivity.id
            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedActivityResponse = updateActivity(
                activityId, "swimming", 23.5, 180.5, DateTime.parse("2024-06-11T05:59:27.258Z")
                , addedUser.id
            )
            assertEquals(204, updatedActivityResponse.status)
            //Assert that the individual fields were all updated as expected
            val retrievedActivityResponse = retrieveActivityByActivityId(addedActivity.id)
            val updatedActivity = jsonNodeToObject<Activity>(retrievedActivityResponse)
            assertEquals("swimming", updatedActivity.description)
            assertEquals(23.5, updatedActivity.duration)
            assertEquals(180.5, updatedActivity.calories)
        }
    }

    @Nested
    inner class DeleteActivities{
        @Test
        fun `deleting an activity by activity id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivityByActivityId(-1).status)
        }
        @Test
        fun `deleting activities by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivitiesByUserId(-1).status)
        }
        @Test
        fun `deleting an activity by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse.status)

            //Act & Assert - delete the added activity and assert a 204 is returned
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)
            assertEquals(204, deleteActivityByActivityId(addedActivity.id).status)
        }
    }


    //helper function to retrieve all activities
    private fun retrieveAllActivities(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/activities").asJson()
    }
    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    //helper function to retrieve activities by user id
    private fun retrieveActivitiesByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/activities/$id").asJson()
    }
    //helper function to delete an activity by activity id
    private fun deleteActivityByActivityId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/activities/$id").asString()
    }
    //helper function to delete an activity by activity id
    private fun deleteActivitiesByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/activities/activity/$id").asString()
    }
    //helper function to add a test user to the database
    private fun updateActivity(id: Int, description: String, duration: Double, calories: Double,
                               started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/activities/$id")
            .body("""
                {
                  "description":"$description",
                  "duration":$duration,
                  "calories":$calories,
                  "started":"$started",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }
    //helper function to delete a test user from the database
    private fun deleteUser (id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }
    //helper function to retrieve activity by activity id
    private fun retrieveActivityByActivityId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/activities/activity/$id").asJson()
    }
    //helper function to retrieve a test user from the database by id
    private fun retrieveUserById(id: Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }
    //helper function to add an activity
    private fun addActivity(description: String, duration: Double, calories: Double,
                            started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/activities")
            .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "calories":$calories,
                   "started":"$started",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }


}