package ie.setu.domain.models

import org.joda.time.DateTime

data class NutritionandCalories( var id: Int, val foodtaken: String, val caloriecount: Double, val foodtakentime: DateTime, val waterintake: Double, val nutrients: Double,val userid: Int)