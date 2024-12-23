package ie.setu.utils
import ie.setu.domain.db.*
import org.jetbrains.exposed.sql.ResultRow
import ie.setu.domain.models.*


fun mapToUser(it: ResultRow) = User (
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    weight = it[Users.weight],
    height = it[Users.height],
    gender = it[Users.gender],
    dateofbirth = it[Users.dateofbirth]
)
fun mapToActivity(it: ResultRow) = Activity (
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    calories = it[Activities.calories],
    started = it[Activities.started],
    userId = it[Activities.userid]
)
fun mapToAppointmentScheduling(it: ResultRow) = AppointmentScheduling (
    id = it[AppointmentScheduler.id],
    appointmentstartdatetime = it[AppointmentScheduler.appointmentstartdatetime],
    appointmentendtime = it[AppointmentScheduler.appointmentendtime],
    appointmenttype = it[AppointmentScheduler.appointmenttype],
    locationofappointment = it[AppointmentScheduler.locationofappointment],
    reasonforappointment = it[AppointmentScheduler.reasonforappointment],
    appointmentstatus = it[AppointmentScheduler.appointmentstatus],
    patientid = it[AppointmentScheduler.patientid]
)

fun mapToMedicationTracker(it: ResultRow) = MedicationTracking(
    id = it[MedicationTracker.id],
    medicinename = it[MedicationTracker.medicinename],
    datetimeofintake = it[MedicationTracker.datetimeofintake],
    dosage = it[MedicationTracker.dosage],
    medtakenornot = it[MedicationTracker.medtakenornot],
    patientid = it[MedicationTracker.patientid]
)

fun mapToLabReport(it: ResultRow) = LabReport(
    id = it[Labreports.id],
    reportname = it[Labreports.reportname],
    reporttype = it[Labreports.reporttype],
    reportsource = it[Labreports.reportsource],
    patientid = it[Labreports.patientid]
)
fun mapToNutritionandCalories(it: ResultRow) = NutritionandCalories(
    id = it[NutritionandCalorie.id],
    foodtaken = it[NutritionandCalorie.foodtaken],
    caloriecount = it[NutritionandCalorie.caloriecount],
    foodtakentime = it[NutritionandCalorie.foodtakentime],
    waterintake = it[NutritionandCalorie.waterintake],
    nutrients = it[NutritionandCalorie.nutrients],
    userid = it[NutritionandCalorie.userid]
)