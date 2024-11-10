package ie.setu.domain.models

import java.sql.Timestamp

data class NutritionandCalories( val id: Int, val foodtaken: String, val caloriecount: Double, val foodtakentime: Timestamp, val waterintake: Double, val nutrients: Double,val userid: Int)