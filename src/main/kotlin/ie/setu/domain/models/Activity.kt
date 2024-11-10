package ie.setu.domain.models

import java.sql.Time
import java.sql.Timestamp

data class Activity(val id: Int, val Description: String, val Duration: Timestamp, val Calories: Double,val Started: Time, val userid: Int )