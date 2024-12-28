package ie.setu.controllers
import ie.setu.domain.respository.NutritionandCalorieDAO
import ie.setu.domain.respository.UserDAO
import ie.setu.domain.models.NutritionandCalories
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context

object NutritionandCalorieController {
    private val userDao = UserDAO()
    private val nutrionandCalorieDAO = NutritionandCalorieDAO()
    //gets all records from nutrionandCalories table
    fun getAll(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(nutrionandCalorieDAO.getAll()))
    }
    //gets specific record for a user from the db table
    fun getNutritionAndCalorieByUserId(ctx: Context) {
        if(userDao.getById(ctx.pathParam("user-id").toInt()) !=null){
            val ncrecords = nutrionandCalorieDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if(ncrecords != null){
                val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(ncrecords))
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }
        }else{
            ctx.status(404)
        }
    }
    //adds nutrionandCalorie record in db table
    fun addnutrionandCalorie(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val newNCRecord = mapper.readValue<NutritionandCalories>(ctx.body())
        if(newNCRecord != null) {
            val ncRecordID = nutrionandCalorieDAO.save(newNCRecord)
            newNCRecord.id = ncRecordID
            ctx.json(newNCRecord)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }
    //updates nutrionandCalorie record by id in db table
    fun updateNutrionAndCalorie(ctx: Context) {
        val existingNCRecord = nutrionandCalorieDAO.getNutritionandCaloriesById(ctx.pathParam("nc-id").toInt())
        if(existingNCRecord != null) {
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            val updatedNCRecord = mapper.readValue<NutritionandCalories>(ctx.body())
            if(updatedNCRecord != null) {
                nutrionandCalorieDAO.update(existingNCRecord.id, updatedNCRecord)
                ctx.json(updatedNCRecord)
                ctx.status(204)
            }else{
                ctx.status(404)
            }
        }else{
            ctx.status(404)
        }
    }
    // deletes nutrionandCalorie record by id in db table
    fun deleteNutrionAndCalorie(ctx: Context) {
        val existingNCRecord = nutrionandCalorieDAO.getNutritionandCaloriesById(ctx.pathParam("nc-id").toInt())
        if(existingNCRecord != null) {
            nutrionandCalorieDAO.deleteNutritionandCalorieById(existingNCRecord.id)
            ctx.status(204)
        }else{
            ctx.status(404)
        }
    }
    // deletes nutrionandCalorie record by userid in db table
    fun deleteNutrionAndCalorieByUserId(ctx: Context) {
        if(nutrionandCalorieDAO.deleteNutritionandCalorieByUserId(ctx.pathParam("user-id").toInt()) != 0){
            ctx.status(204)
        }else{
            ctx.status(404)
        }
    }
    // gets nutrionandCalorie by id from db table
    fun getnutrionandCalorieById(ctx: Context) {
        val existingNCRecord = nutrionandCalorieDAO.getNutritionandCaloriesById(ctx.pathParam("nc-id").toInt())
        if(existingNCRecord != null) {
            //nutrionandCalorieDAO.getNutritionandCaloriesById(ctx.pathParam("nc-id").toInt())
            ctx.json(existingNCRecord)
            ctx.status(200)
        }else{
            ctx.status(404)
        }
    }
}