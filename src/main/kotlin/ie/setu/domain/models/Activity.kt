package ie.setu.domain.models

import org.joda.time.DateTime


data class Activity(val id: Int, val Description: String, val Duration: Double, val Calories: Double,val Started: DateTime, val userid: Int )