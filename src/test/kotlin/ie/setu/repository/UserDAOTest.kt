package ie.setu.repository
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import ie.setu.helpers.users
import ie.setu.domain.models.User
import org.junit.jupiter.api.Test
import ie.setu.domain.respository.UserDAO
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import ie.setu.helpers.populateUserTable
import org.junit.jupiter.api.Nested
import ie.setu.helpers.nonExistingEmail

//retrieving some test data from Fixtures
val user1 = users[0]
val user2 = users[1]
val user3 = users[2]

class UserDAOTest {
    companion object {
        //Make a connection to a local, in memory H2 database
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }

    }
    @Test
    fun `getting all users from a populated table returns all rows`() {
        transaction {
            //Arrange - create and populate table with three users
            val userDAO = populateUserTable()
            //Act & Assert
            assertEquals(3, userDAO.getAll().size)
        }
    }
    @Test
    fun `get user by id that doesn't exist, results in no user returned`() {
        transaction {
            //Arrange - create and populate table with three users
            val userDAO = populateUserTable()

            //Act & Assert
            assertEquals(null, userDAO.getById(4))
        }
    }
    @Test
    fun `get user by id that exists, results in a correct user returned`() {
        transaction {
            //Arrange - create and populate table with three users
            val userDAO = populateUserTable()

            //Act & Assert
            assertEquals(user1, userDAO.getById(user1.id))
        }

    }
    @Test
    fun `get all users over empty table returns none`() {
        transaction {

            //Arrange - create and setup userDAO object
            SchemaUtils.create(Users)
            val userDAO = UserDAO()

            //Act & Assert
            assertEquals(0, userDAO.getAll().size)
        }
    }
    @Test
    fun `get user by email that doesn't exist, results in no user returned`() {
        transaction {

            //Arrange - create and populate table with three users
            val userDAO = populateUserTable()

            //Act & Assert
            assertEquals(null, userDAO.getuserbyemaiid(nonExistingEmail))
        }
    }
    @Test
    fun `get user by email that exists, results in correct user returned`() {
        transaction {

            //Arrange - create and populate table with three users
            val userDAO = populateUserTable()

            //Act & Assert
            assertEquals(user2, userDAO.getuserbyemaiid(user2.email))
        }
    }
    @Nested
    inner class CreateUsers {
        @Test
        fun `multiple users added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                assertEquals(user1, userDAO.getById(user1.id))
                assertEquals(user2, userDAO.getById(user2.id))
                assertEquals(user3, userDAO.getById(user3.id))
            }
        }
    }
    @Nested
    inner class UpdateUsers {
        @Test
        fun `updating existing user in table results in successful update`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                val user3Updated = User(id =3, name = "Anita J", email = "anita@james.com", weight = 65.2, height = 170.2, gender = "Female", dateofbirth = "1993-03-04")
                userDAO.updateUser(user3.id, user3Updated)
                assertEquals(user3Updated, userDAO.getById(3))
            }
        }
        @Test
        fun `updating non-existant user in table results in no updates`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                val user4Updated = User(id =4, name = "Anita H", email = "anita@Hammer.com", weight = 65.2, height = 170.2, gender = "Female", dateofbirth = "1993-03-04")
                userDAO.updateUser(4, user4Updated)
                assertEquals(null, userDAO.getById(4))
                assertEquals(3, userDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteUsers {
        @Test
        fun `deleting a non-existant user in table results in no deletion`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                userDAO.deleteUser(4)
                assertEquals(3, userDAO.getAll().size)
            }
        }
        @Test
        fun `deleting an existing user in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                userDAO.deleteUser(user3.id)
                assertEquals(2, userDAO.getAll().size)
            }
        }
    }

}