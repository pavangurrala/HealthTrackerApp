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
        app.get("/api/users/{email}",UserController::getUserByEmail)
        app.post("/api/users",UserController::addUser)
        app.patch("/api/users/{user-id}",UserController::updateUser)
        app.delete("/api/users/{user-id}", UserController::deleteUser)
        app.get("/api/activities", ActivityController::getAllActivities)
        app.get("/api/activities/{user-id}",ActivityController::getactivityByUserID)
        app.patch("/api/activities/{act-id}",ActivityController::updateActivity)
        app.delete("/api/activities/{act-id}",ActivityController::deleteActivity)
        app.post("/api/activities",ActivityController::addActivity)
        //app.get("api/activities/{act-id}",ActivityController::getActivityByID)
        app.get("/api/appointmentscheduler",AppointmentController::getAllAppointments)
        app.get("/api/appointmentscheduler/{user-id}",AppointmentController::getAppointmentByUserId)
        app.post("/api/appointmentscheduler",AppointmentController::addAppointment)
        app.patch("/api/appointmentscheduler/{appointment-id}",AppointmentController::updateAppointment)
        app.delete("/api/appointmentscheduler/{appointment-id}",AppointmentController::deleteAppointment)
        app.get("/api/medicationtracker", MedicationTrackingController::getAllMedications)
        app.get("/api/medicationtracker/{user-id}",MedicationTrackingController::getMedicationbyUserID)
        app.post("/api/medicationtracker", MedicationTrackingController::addMedication)
        app.patch("/api/medicationtracker/{med-id}", MedicationTrackingController::updateMedication)
        app.delete("/api/medicationtracker/{med-id}", MedicationTrackingController::deleteMedication)
        app.get("/api/labreports", LabReportController::getAllLabReports)
        app.get("/api/labreports/{user-id}", LabReportController::getLabReportsByUserID)
        app.post("/api/labreports", LabReportController::addLabReport)
        app.patch("/api/labreports/{lab-id}", LabReportController::updateLabReport)
        app.delete("/api/labreports/{lab-id}", LabReportController::deleteLabReport)
    }
}