package ie.setu.domain.models

import org.joda.time.DateTime





data class Activity(var id: Int, val description: String, val duration: Double, val calories: Double,val started: DateTime, val userId: Int )