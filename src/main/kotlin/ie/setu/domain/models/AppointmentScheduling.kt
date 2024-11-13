package ie.setu.domain.models

import org.joda.time.DateTime



data class AppointmentScheduling (val id : Int,  val appointmentstartdatetime: DateTime, val appointmentendtime: DateTime, val appointmenttype : String, val locationofappointment : String, val reasonforappointment : String, val appointmentstatus: String, val patientid : Int)