package ie.setu.domain.models

import org.joda.time.DateTime

data class MedicationTracking(val id : Int , val medicinename : String, val datetimeofintake:DateTime, val dosage: String, val medtakenornot : String,val patientid : Int)