package ie.setu.controllers
import ie.setu.config.DbConfig
import ie.setu.helpers.ServerContainer
import org.junit.jupiter.api.TestInstance
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import ie.setu.domain.models.User
import kong.unirest.core.HttpResponse
import kong.unirest.core.JsonNode
import kong.unirest.core.Unirest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import ie.setu.helpers.nonExistingEmail
import ie.setu.helpers.validName
import ie.setu.helpers.validEmail
import ie.setu.helpers.validheight
import ie.setu.helpers.validweight
import ie.setu.helpers.validgender
import ie.setu.helpers.validdateofbirth


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class ReadUSers{
        @Test
        fun `get all users from the database returns 200 or 404 response`() {
            val response = Unirest.get(origin + "/api/users").asString()
            if (response.status == 200) {
                val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedUsers.size)
            }
            else {
                assertEquals(404, response.status)
            }
        }
        @Test
        fun `get user by id when user does not exist returns 404 response`() {

            //Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = retrieveUserById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }
        @Test
        fun `get user by email when user does not exist returns 404 response`() {
            // Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = Unirest.get(origin + "/api/users/email/${nonExistingEmail}").asString()
            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }
    }
    @Nested
    inner class CreateUser{
        @Test
        fun `create a new user with all the details returns 200 response`() {
            //Arrange & Act & Assert
            //    add the user and verify return code (using fixture data)
            val addResponse = addUser(6,validName, validEmail, validweight,validheight, validgender, validdateofbirth)
            assertEquals(201, addResponse.status)

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = retrieveUserByEmail(validEmail)
            assertEquals(200, retrieveResponse.status)
            //Assert - verify the contents of the retrieved user
            val retrievedUser:User = jsonToObject(addResponse.body.toString())
            assertEquals(validEmail, retrievedUser.email)
            assertEquals(validName, retrievedUser.name)
            //After - restore the db to previous state by deleting the added user
            val deleteResponse = deleteUser(retrievedUser.id)
            assertEquals(204, deleteResponse.status)

        }
    }
    @Nested
    inner class UpdateUser{
        @Test
        fun `update a existing user with all the details returns 204 response`() {
            //Arrange - add the user that we plan to do an update on
            val addedResponse = addUser(6,validName, validEmail, validweight,validheight, validgender, validdateofbirth)
            val addedUser : User = jsonToObject(addedResponse.body.toString())
            //Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(204, updateUser(addedUser.id, "Carol S", "carol@singernew.com", validweight, validheight,validgender,validdateofbirth).status)
            //Act & Assert - retrieve updated user and assert details are correct
            val updatedUserResponse = retrieveUserById(addedUser.id)
            val updatedUser : User = jsonToObject(updatedUserResponse.body.toString())
            assertEquals("Carol S", updatedUser.name)
            assertEquals("carol@singernew.com", updatedUser.email)
            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }
        @Test
        fun `updating a user when it doesn't exist, returns a 404 response`() {

            //Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(404, updateUser(7, "Carol S", "carol@singernew.com", validweight, validheight,validgender,validdateofbirth).status)
        }
    }
    @Nested
    inner class DeleteUser{
        @Test
        fun `delete a exisiting user when it doesn't exist returns 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404,deleteUser(-1).status)
        }

        @Test
        fun `delete a exisiting user with all the details returns 204 response`() {
            //Arrange - add the user that we plan to do an update on
            val addedResponse = addUser(6,validName, validEmail, validweight,validheight, validgender, validdateofbirth)
            val addedUser : User = jsonToObject(addedResponse.body.toString())
            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted user --> 404 response
            assertEquals(404, retrieveUserById(addedUser.id).status)
        }
    }
    //helper function to add a test user to the database
    private fun addUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }
    //helper function to delete a test user from the database
    private fun deleteUser (id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }
    //helper function to retrieve a test user from the database by email
    private fun retrieveUserByEmail(email : String) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/email/${email}").asString()
    }
    //helper function to retrieve a test user from the database by id
    private fun retrieveUserById(id: Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }
    //helper function to add a test user to the database
    private fun updateUser (id:Int,name: String, email: String, weight: Double, height:Double, gender:String,dateofbirth:String): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/users/$id")
            .body("{\"id\":\"$id\",\"name\":\"$name\", \"email\":\"$email\", \"weight\":\"$weight\", \"height\":\"$height\", \"gender\":\"$gender\",\"dateofbirth\":\"$dateofbirth\"}")
            .asJson()
    }


}


