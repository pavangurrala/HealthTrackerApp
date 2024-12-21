package ie.setu.repository
import ie.setu.domain.db.NutritionandCalorie
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import ie.setu.helpers.nutritionandcalories
import ie.setu.domain.models.NutritionandCalories
import org.junit.jupiter.api.Test
import ie.setu.domain.respository.NutritionandCalorieDAO
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.populateNutritionandCalorieTable
import org.junit.jupiter.api.Nested
import org.joda.time.DateTime
class NutritionandCalorieDAOTest {
    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    private val nc1 = nutritionandcalories.get(0)
    private val nc2 = nutritionandcalories.get(1)
    private val nc3 = nutritionandcalories.get(2)
    @Nested
    inner class createNutrionandCaloriesRecords{
        @Test
        fun `multiple nutrionandcaloriesrecords added to table can be retrieved successfully`(){
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
                assertEquals(nc1,nutritionandCalorieDAO.getNutritionandCaloriesById(nc1.id))
                assertEquals(nc2,nutritionandCalorieDAO.getNutritionandCaloriesById(nc2.id))
                assertEquals(nc3,nutritionandCalorieDAO.getNutritionandCaloriesById(nc3.id))
            }
        }
    }
    @Nested
    inner class ReadNutritionandCaloriesRecords{
        @Test
        fun `getting all nutrionandcaloriesrecords from a populated table returns all rows`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
            }
        }
        @Test
        fun `get nutrionandcalories by user id that has no nutrionandcalories records, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(0,nutritionandCalorieDAO.findByUserId(3).size)
            }
        }
        @Test
        fun `get nutrionandcalories records by user id that exists, results in a correct nutrionandcalories record(s) returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(nc1,nutritionandCalorieDAO.findByUserId(1).get(0))
                assertEquals(nc2,nutritionandCalorieDAO.findByUserId(1).get(1))
                assertEquals(nc3,nutritionandCalorieDAO.findByUserId(2).get(0))
            }
        }
        @Test
        fun `get all nutrionandcalories over empty table returns none`(){
            transaction {
                //Arrange - create and setup calorieDAO object
                SchemaUtils.create(NutritionandCalorie)
                val calorieDAO = NutritionandCalorieDAO()
                //Act & Assert
                assertEquals(0,calorieDAO.getAll().size)
            }
        }
        @Test
        fun `get nutrionandcalories by nutrionandcalories id that has no records, results in no record returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(null,nutritionandCalorieDAO.getNutritionandCaloriesById(4))
            }
        }
        @Test
        fun `get nutrionandcalories by nutrionandcalories id that exists, results in a correct nutrionandcalories returned`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(nc1,nutritionandCalorieDAO.getNutritionandCaloriesById(nc1.id))
                assertEquals(nc3,nutritionandCalorieDAO.getNutritionandCaloriesById(nc3.id))
            }
        }
    }
    @Nested
    inner class UpdateNutritionandCaloriesRecords{
        @Test
        fun `updating existing nutrionandcalories record in table results in successful update`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                val updatednutrionandcalories = NutritionandCalories(id=1, foodtaken = "Curry", caloriecount = 85.2, foodtakentime = DateTime.parse("2024-11-15T07:30:00.000"), waterintake = 4.3, nutrients = 35.2, userid = 1)
                nutritionandCalorieDAO.update(updatednutrionandcalories.id, updatednutrionandcalories)
                assertEquals(updatednutrionandcalories, nutritionandCalorieDAO.getNutritionandCaloriesById(1))
            }
        }
        @Test
        fun `updating non-existant nutrionandcalories record in table results in no updates`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                val updatednutrionandcalories = NutritionandCalories(id=5, foodtaken = "Curry", caloriecount = 85.2, foodtakentime = DateTime.parse("2024-11-15T07:30:00.000"), waterintake = 4.3, nutrients = 35.2, userid = 1)
                nutritionandCalorieDAO.update(5, updatednutrionandcalories)
                assertEquals(null,nutritionandCalorieDAO.getNutritionandCaloriesById(5))
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteNutritionandCaloriesRecords{
        @Test
        fun `deleting an existing nutrionandcalories record (by nutrionandcalories record id) in table results in record being deleted`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
                nutritionandCalorieDAO.deleteNutritionandCalorieById(nc2.id)
                assertEquals(2,nutritionandCalorieDAO.getAll().size)
            }
        }
        @Test
        fun `deleting a non-existant nutrionandcalories record (by nutrionandcalories record id) in table results in no deletion`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
                nutritionandCalorieDAO.deleteNutritionandCalorieById(5)
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
            }
        }
        @Test
        fun `deleting nutrionandcalories records when 1 or more exist for user id results in deletion`(){
            transaction {
                //Arrange - create and populate tables with three users and three nutrionandcalories records
                val userDAO = populateUserTable()
                val nutritionandCalorieDAO = populateNutritionandCalorieTable()
                //Act & Assert
                assertEquals(3,nutritionandCalorieDAO.getAll().size)
                nutritionandCalorieDAO.deleteNutritionandCalorieByUserId(1)
                assertEquals(1,nutritionandCalorieDAO.getAll().size)
            }
        }
    }
}