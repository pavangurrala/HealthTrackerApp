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
                ctx.status(201)
            }else
            {
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }
    //Add new appointment to the DB
    fun addAppointment(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val newappointment = mapper.readValue<AppointmentScheduling>(ctx.body())
        val appointmentID = appointmentDao.save(newappointment)
        newappointment.id = appointmentID
        ctx.json(newappointment)
        ctx.status(201)
    }
    //update existing appointment by ID
    fun updateAppointment(ctx: Context) {
        val appointment = appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
        if(appointment != null){
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            val updatedappointment = mapper.readValue<AppointmentScheduling>(ctx.body())
            appointmentDao.update(appointment.id,updatedappointment)
            ctx.status(200)
        }else{
            ctx.status(404)
        }
    }
    //delete the existing appointment by ID
    fun deleteAppointment(ctx: Context) {
        val appointment = appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
        if(appointment != null){
            appointmentDao.delete(appointment.id)
            ctx.status(204)
        }else{
            ctx.status(404)
        }
    }
    //Deletes appointment of specific user
    fun deleteAppointmentByUserId(ctx: Context) {
        if(appointmentDao.deleteappointmentbyuserid(ctx.pathParam("user-id").toInt()) != 0){
            ctx.status(204)
        }else{
            ctx.status(404)
        }
    }
    //gets appointment of specific user
    fun getappointmentbyID(ctx: Context) {
        val appointment = appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
        if(appointment != null){
            appointmentDao.getAppointmentById(ctx.pathParam("appointment-id").toInt())
            ctx.json(appointment)
            ctx.status(200)
        }else{
            ctx.status(404)
        }
    }
}