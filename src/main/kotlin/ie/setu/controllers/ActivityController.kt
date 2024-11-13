package ie.setu.controllers
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import ie.setu.domain.models.Activity
import ie.setu.domain.respository.ActivityDAO
import ie.setu.domain.respository.UserDAO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context

object ActivityController {
    private val activityDao = ActivityDAO()
    private val userDao = UserDAO()
    //method to get all activities in db while mapper handles deserialization of Joda time into a string
    fun getAllActivities(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(activityDao.getAll()))
    }
    //get activities for specific user by ID
    fun getactivityByUserID(ctx: Context) {
        if(userDao.getById(ctx.pathParam("user-id").toInt()) !=null){
            val activities = activityDao.findByUserId(ctx.pathParam("user-id").toInt())
            if(activities!=null){
                val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }
    // update the existing the activity by Activity ID
    fun updateActivity(ctx: Context) {
        val activity = activityDao.findByActivityId(ctx.pathParam("act-id").toInt())
        if(activity!=null){
             // val user = mapper.readValue<User>(ctx.body())
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            val newactivity =mapper.readValue<Activity>(ctx.body())
            ctx.json(mapper.writeValueAsString(activityDao.updateActivity(activity.id, newactivity)))
        }
    }
    //add new Activity to the DB
    fun addActivity(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity = mapper.readValue<Activity>(ctx.body())
        activityDao.save(activity)
        ctx.json(activity)
    }
    //delete the existing Activity by ActivityID from DB
    fun deleteActivity(ctx: Context) {
        val activity = activityDao.deleteactivity(ctx.pathParam("act-id").toInt())
        ctx.status(204)
    }
    // Get existng activity by ID from the DB
    fun getActivityByID(ctx: Context) {
        val activity = activityDao.findByActivityId(ctx.pathParam("act-id").toInt())
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(activity))
    }

}