package ie.setu.domain.respository
import ie.setu.domain.models.NutritionandCalories
import ie.setu.domain.db.NutritionandCalorie
import ie.setu.utils.mapToNutritionandCalories
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
class NutritionandCalorieDAO {
    //get all the records in nutritionandcalories table
    fun getAll():ArrayList<NutritionandCalories>{
        val nutritionandCaloriesList: ArrayList<NutritionandCalories> = arrayListOf()
        transaction {
            NutritionandCalorie.selectAll().map {
                nutritionandCaloriesList.add(mapToNutritionandCalories(it))
            }
        }
        return nutritionandCaloriesList
    }
    //get the record specific record based on the id
    fun getNutritionandCaloriesById(id:Int):NutritionandCalories?{
        return transaction {
            NutritionandCalorie.selectAll().where{NutritionandCalorie.id eq id}.map { mapToNutritionandCalories(it) }.firstOrNull()
        }
    }
    //get the specific record based on the user id
    fun findByUserId(userId:Int):List<NutritionandCalories>{
        return transaction {
            NutritionandCalorie.selectAll().where{NutritionandCalorie.userid eq userId}.map { mapToNutritionandCalories(it) }
        }
    }
    fun save(nutritionandCalories: NutritionandCalories):Int{
        return transaction {
            val nutritionandcaloriesid = NutritionandCalorie.insert{
                it[NutritionandCalorie.foodtaken] = nutritionandCalories.foodtaken
                it[NutritionandCalorie.caloriecount] = nutritionandCalories.caloriecount
                it[NutritionandCalorie.foodtakentime] = nutritionandCalories.foodtakentime
                it[NutritionandCalorie.waterintake] = nutritionandCalories.waterintake
                it[NutritionandCalorie.nutrients] = nutritionandCalories.nutrients
                it[NutritionandCalorie.userid] = nutritionandCalories.userid
            } get NutritionandCalorie.id
            nutritionandcaloriesid
        }
    }
    fun update(id: Int,nutritionandCalories: NutritionandCalories):Int{
        return transaction {
            NutritionandCalorie.update({ NutritionandCalorie.id eq id }){
                it[NutritionandCalorie.foodtaken] = nutritionandCalories.foodtaken
                it[NutritionandCalorie.caloriecount] = nutritionandCalories.caloriecount
                it[NutritionandCalorie.foodtakentime] = nutritionandCalories.foodtakentime
                it[NutritionandCalorie.waterintake] = nutritionandCalories.waterintake
                it[NutritionandCalorie.nutrients] = nutritionandCalories.nutrients
                it[NutritionandCalorie.userid] = nutritionandCalories.userid
            }
        }
    }
    fun deleteNutritionandCalorieById(id:Int):Int{
        return transaction {
            NutritionandCalorie.deleteWhere{NutritionandCalorie.id eq id}
        }
    }
    fun deleteNutritionandCalorieByUserId(userId:Int):Int{
        return transaction {
            NutritionandCalorie.deleteWhere{NutritionandCalorie.userid eq userId}
        }
    }
}