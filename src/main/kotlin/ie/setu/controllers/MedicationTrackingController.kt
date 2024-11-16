package ie.setu.controllers
import ie.setu.domain.db.MedicationTracker
import ie.setu.domain.models.MedicationTracking
import ie.setu.domain.respository.UserDAO
import ie.setu.domain.respository.MedicationTrackerDAO
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.Context
object MedicationTrackingController {
    private val userDao = UserDAO()
    private val medicationTrackerDao = MedicationTrackerDAO()
    //gets all the medication records from the db
    fun getAllMedications(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(medicationTrackerDao.getAll())
    }
    fun getMedicationbyUserID(ctx: Context) {
        if(userDao.getById(ctx.pathParam("user-id").toInt()) !=null){
            val medications = medicationTrackerDao.getMedicationTrackerByUserID(ctx.pathParam("user-id").toInt())
            if(medications!=null){
                val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(medications))
            }
        }
    }
    fun addMedication(ctx: Context) {
        val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val medication = mapper.readValue<MedicationTracking>(ctx.body())
        medicationTrackerDao.save(medication)
        ctx.json(medication)
    }
    fun updateMedication(ctx: Context) {
        val currentmedication = medicationTrackerDao.getMedicationTrackerById(ctx.pathParam("med-id").toInt())
        if(currentmedication!=null){
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            val updatedmedication = mapper.readValue<MedicationTracking>(ctx.body())
            ctx.json(mapper.writeValueAsString(medicationTrackerDao.updateMedicationTracking(currentmedication.id,updatedmedication)))
        }
    }
    fun deleteMedication(ctx: Context) {
        val medication = medicationTrackerDao.getMedicationTrackerById(ctx.pathParam("med-id").toInt())
        if(medication!=null){
            medicationTrackerDao.deleteMedicationTracking(ctx.pathParam("med-id").toInt())
            ctx.status(204)
        }
    }
    fun deleteMedicationByUserID(ctx: Context) {
        if(medicationTrackerDao.deleteMedicationTrackingByUserID(ctx.pathParam("user-id").toInt())!=0){
            ctx.status(204)
        }else{
            ctx.status(400)
        }
    }
    fun getMedicationTrackerById(ctx: Context) {
        val medicationRecord = medicationTrackerDao.getMedicationTrackerById(ctx.pathParam("med-id").toInt())
        if(medicationRecord!=null){
            val mapper = jacksonObjectMapper().registerModule(JodaModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ctx.json(mapper.writeValueAsString(medicationTrackerDao.getMedicationTrackerById(medicationRecord.id) ))
            ctx.status(200)
        }else{
            ctx.status(400)
        }
    }
}