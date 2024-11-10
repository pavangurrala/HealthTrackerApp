package ie.setu.domain.models

import java.sql.Time
import java.util.Date

data class AppointmentScheduling (val patientid : Int, val appointmentid : Int, val dateofbirth : Date, val appointment_date : Date, val appointmentstarttime: Time, val appointmentendtime: Time, val appointmenttype : String, val locationofappointment : String, val reasonforappointment : String, val appointmentstatus: String)