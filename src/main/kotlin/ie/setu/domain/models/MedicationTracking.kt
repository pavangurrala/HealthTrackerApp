package ie.setu.domain.models

import java.sql.Time
import java.util.Date

data class MedicationTracking(val medicationtrackingid : Int ,val patientid : Int, val medicinename : String, val timeofintake:Time, val dateofintake: Date, val dosage: String, val medtakenornot : String)