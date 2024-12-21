package ie.setu.domain.db
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
object NutritionandCalorie : Table("nutritionandcalorie") {
    val id = integer(name = "id").autoIncrement()
    val foodtaken = varchar("foodtaken", 255)
    val caloriecount = double("caloriecount")
    val foodtakentime = datetime("foodtakentime")
    val waterintake = double("waterintake")
    val nutrients = double("nutrients")
    val userid = integer("userid").references(Users.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(NutritionandCalorie.id, name = "PK_NutritionandCalorie_ID")

}