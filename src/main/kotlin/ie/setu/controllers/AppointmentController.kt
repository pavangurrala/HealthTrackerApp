package ie.setu.controllers
import ie.setu.domain.db.AppointmentScheduler
import ie.setu.utils.mapToAppointmentScheduling
import ie.setu.domain.respository.AppointmentDAO
import ie.setu.domain.respository.UserDAO
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.models.AppointmentScheduling
import io.javalin.http.Context

object AppointmentController {
    private val userDao = UserDAO()
    private val appointmentDao = AppointmentDAO()
    //get all the appointments irrespective of the parameters
    fun getAllAppointments(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(appointmentDao.getAll()))
    }
    //get appointment for a specific user ID from DB
    fun getAppointmentByUserId(ctx: Context) {
        if(userDao.getById(ctx.pathParam("user-id").toInt()) !=null){
            val appointments = appointmentDao.getappointmentByUserId(ctx.pathParam("user-id").toInt())
            if(appointments!=null){
                val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(appointments))
            }
        }
    }
    //Add new appointment to the DB
    fun addAppointment(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val newappointment = mapper.readValue<AppointmentScheduling>(ctx.body())
        if(newappointment != null){
            appointmentDao.save(newappointment)
            ctx.json(newappointment)
        }
    }
    //update existing appointment by ID
    fun updateAppointment(ctx: Context) {
        val appointment = appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
        if(appointment != null){
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            val updatedappointment = mapper.readValue<AppointmentScheduling>(ctx.body())
            appointmentDao.update(appointment.id,updatedappointment)
        }
    }
    //delete the existing appointment by ID
    fun deleteAppointment(ctx: Context) {
        val appointment = appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
        if(appointment != null){
            appointmentDao.delete(appointment.id)
        }
    }
}