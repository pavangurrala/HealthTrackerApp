package ie.setu.helpers


import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.db.AppointmentScheduler
import ie.setu.domain.db.MedicationTracker
import ie.setu.domain.db.Labreports
import ie.setu.domain.models.Activity
import ie.setu.domain.models.User
import ie.setu.domain.models.AppointmentScheduling
import ie.setu.domain.models.MedicationTracking
import ie.setu.domain.models.LabReport
import ie.setu.domain.respository.UserDAO
import ie.setu.domain.respository.ActivityDAO
import ie.setu.domain.respository.AppointmentDAO
import ie.setu.domain.respository.MedicationTrackerDAO
import ie.setu.domain.respository.LabReportDAO
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime
val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Carol Singer"
val validEmail = "carol@singer.com"
val validweight = 65.2
val validheight = 165.2
val validgender = "Female"
val validdateofbirth = "1995-03-12"

val users = arrayListOf<User>(
    User(id =1, name = "John T", email = "john@test.com", weight = 85.2, height = 180.2, gender = "Male", dateofbirth = "1990-01-01"),
    User(id =2, name = "Jerry H", email = "jerry@test.com", weight = 86.2, height = 178.2, gender = "Male", dateofbirth = "1992-02-02"),
    User(id =3, name = "Anita M", email = "anita@test.com", weight = 65.2, height = 170.2, gender = "Female", dateofbirth = "1993-03-04"),
    User(id =4, name = "Amanda C", email = "amanda@test.com", weight = 63.2, height = 170.2, gender = "Female", dateofbirth = "1993-03-04"),
    User(id =5, name = "Carol S", email = "carol@test.com", weight = 62.2, height = 170.2, gender = "Female", dateofbirth = "1993-03-04"),
)
val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230.2, started = DateTime.now(), userId = 1),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80.5, started = DateTime.now(), userId = 1),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120.5, started = DateTime.now(), userId = 2)
)
val appointments = arrayListOf<AppointmentScheduling>(
    AppointmentScheduling(id=1, appointmentstartdatetime = DateTime.parse("2024-11-15T06:00:00.000"), appointmentendtime = DateTime.parse("2024-11-15T06:30:00.000"), appointmentstatus = "Booked", appointmenttype = "Online", locationofappointment = "Hospital", reasonforappointment = "Fever", patientid = 1),
    AppointmentScheduling(id=2, appointmentstartdatetime = DateTime.parse("2024-11-15T07:00:00.000"), appointmentendtime = DateTime.parse("2024-11-15T07:30:00.000"), appointmentstatus = "Booked", appointmenttype = "Walkin", locationofappointment = "Hospital", reasonforappointment = "FollowUp", patientid = 1),
    AppointmentScheduling(id=3, appointmentstartdatetime = DateTime.parse("2024-11-15T07:30:00.000"), appointmentendtime = DateTime.parse("2024-11-15T08:00:00.000"), appointmentstatus = "Booked", appointmenttype = "Online", locationofappointment = "Hospital", reasonforappointment = "General Checkup", patientid = 2)
)
val medicationTracker = arrayListOf<MedicationTracking>(
    MedicationTracking(id=1,medicinename = "Paracetamol", datetimeofintake = DateTime.parse("2024-11-15T07:30:00.000"), dosage = "1-tab-500mg", medtakenornot = "1Tab in the morning", patientid = 1),
    MedicationTracking(id=2,medicinename = "Aspirin", datetimeofintake = DateTime.parse("2024-11-15T07:30:00.000"), dosage = "1-tab-500mg", medtakenornot = "1Tab in the morning", patientid = 1),
    MedicationTracking(id=3,medicinename = "Paracetamol", datetimeofintake = DateTime.parse("2024-11-15T07:30:00.000"), dosage = "1-tab-500mg", medtakenornot = "1Tab in the morning", patientid = 2)
)
val labreport = arrayListOf<LabReport>(
    LabReport(id=1,reportname = "Report_name_mri", reporttype = "MRI Scan", reportsource = "ABC_Diagnostics", patientid = 1),
    LabReport(id=2,reportname = "Report_name_br", reporttype = "Blood Test", reportsource = "ANC_Hospital", patientid = 1),
    LabReport(id=3,reportname = "Report_name_ct", reporttype = "CT Scan", reportsource = "ABC_Diagnostics", patientid = 2)
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.saveUser(users[0])
    userDAO.saveUser(users[1])
    userDAO.saveUser(users[2])
    return userDAO
}
fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities[0])
    activityDAO.save(activities[1])
    activityDAO.save(activities[2])
    return activityDAO
}
fun populateAppointmentTable(): AppointmentDAO {
    SchemaUtils.create(AppointmentScheduler)
    val appointmentDAO = AppointmentDAO()
    appointmentDAO.save(appointments[0])
    appointmentDAO.save(appointments[1])
    appointmentDAO.save(appointments[2])
    return appointmentDAO
}
fun populateMedicationTable():MedicationTrackerDAO{
    SchemaUtils.create(MedicationTracker)
    val medicationDAO = MedicationTrackerDAO()
    medicationDAO.save(medicationTracker[0])
    medicationDAO.save(medicationTracker[1])
    medicationDAO.save(medicationTracker[2])
    return medicationDAO
}
fun populateLabReportTable(): LabReportDAO {
    SchemaUtils.create(Labreports)
    val labReportDAO = LabReportDAO()
    labReportDAO.addLabReports(labreport[0])
    labReportDAO.addLabReports(labreport[1])
    labReportDAO.addLabReports(labreport[2])
    return labReportDAO
}


