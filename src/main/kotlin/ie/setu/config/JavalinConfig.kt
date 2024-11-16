package ie.setu.config

import ie.setu.controllers.ActivityController
import io.javalin.Javalin
import ie.setu.controllers.UserController
import ie.setu.utils.jsonObjectMapper
import io.javalin.json.JavalinJackson
import ie.setu.controllers.AppointmentController
import ie.setu.controllers.MedicationTrackingController
import ie.setu.controllers.LabReportController

class JavalinConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create{
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
        }.apply {
            exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404-Not Found") }
        }
            .start(7001)
        registerRoutes(app)
        return app

    }

    private fun registerRoutes(app: Javalin) {
        app.get("/api/users", UserController::getAllUsers)
        app.get("/api/users/{user-id}",UserController::getUserById)
        app.get("/api/users/email/{email}",UserController::getUserByEmail)
        app.post("/api/users",UserController::addUser)
        app.patch("/api/users/{user-id}",UserController::updateUser)
        app.delete("/api/users/{user-id}", UserController::deleteUser)
        app.get("/api/activities", ActivityController::getAllActivities)
        app.get("/api/activities/{user-id}",ActivityController::getactivityByUserID)
        app.get("/api/activities/activity/{act-id}", ActivityController::getActivityByID)
        app.patch("/api/activities/{act-id}",ActivityController::updateActivity)
        app.delete("/api/activities/{act-id}",ActivityController::deleteActivity)
        app.post("/api/activities",ActivityController::addActivity)
        app.delete("/api/activities/activity/{user-id}",ActivityController::deleteActivityByUserID)
        app.get("/api/appointmentscheduler",AppointmentController::getAllAppointments)
        app.get("/api/appointmentscheduler/{user-id}",AppointmentController::getAppointmentByUserId)
        app.get("/api/appointmentscheduler/appointment/{appointment-id}",AppointmentController::getappointmentbyID)
        app.post("/api/appointmentscheduler",AppointmentController::addAppointment)
        app.patch("/api/appointmentscheduler/{appointment-id}",AppointmentController::updateAppointment)
        app.delete("/api/appointmentscheduler/{appointment-id}",AppointmentController::deleteAppointment)
        app.delete("/api/appointmentscheduler/appointment/{user-id}",AppointmentController::deleteAppointmentByUserId)
        app.get("/api/medicationtracker", MedicationTrackingController::getAllMedications)
        app.get("/api/medicationtracker/{user-id}",MedicationTrackingController::getMedicationbyUserID)
        app.get("/api/medicationtracker/medicationrecord/{med-id}",MedicationTrackingController::getMedicationTrackerById)
        app.post("/api/medicationtracker", MedicationTrackingController::addMedication)
        app.patch("/api/medicationtracker/{med-id}", MedicationTrackingController::updateMedication)
        app.delete("/api/medicationtracker/{med-id}", MedicationTrackingController::deleteMedication)
        app.delete("/api/medicationtracker/medicationrecord/{user-id}",MedicationTrackingController::deleteMedicationByUserID)
        app.get("/api/labreports", LabReportController::getAllLabReports)
        app.get("/api/labreports/{user-id}", LabReportController::getLabReportsByUserID)
        app.get("/api/labreports/labreport/{lab-id}", LabReportController::getLabReportByID)
        app.post("/api/labreports", LabReportController::addLabReport)
        app.patch("/api/labreports/{lab-id}", LabReportController::updateLabReport)
        app.delete("/api/labreports/{lab-id}", LabReportController::deleteLabReport)
        app.delete("/api/labreports/labreport/{user-id}", LabReportController::deleteReportByUserID)
    }
}