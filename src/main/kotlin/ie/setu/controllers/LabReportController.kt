package ie.setu.controllers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.models.LabReport
import ie.setu.domain.respository.LabReportDAO
import ie.setu.domain.respository.UserDAO
import io.javalin.http.Context
object LabReportController {
    private val userDao = UserDAO()
    private val labReportsDao = LabReportDAO()

    fun getAllLabReports(ctx: Context) {
        ctx.json(labReportsDao.getAll())
    }
    fun getLabReportsByUserID(ctx: Context) {
        if(userDao.getById(ctx.pathParam("user-id").toInt()) !=null){
            val labReports = labReportsDao.getLabReportsByUserID(ctx.pathParam("user-id").toInt())
            if(labReports!=null){
                ctx.json(labReports)
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }
        }else{
            ctx.status(404)
        }
    }
    fun addLabReport(ctx: Context){
        val mapper = jacksonObjectMapper()
        val labreport = mapper.readValue<LabReport>(ctx.body())
        val labreportID = labReportsDao.addLabReports(labreport)
        labreport.id = labreportID
        ctx.json(labreport)
        ctx.status(201)

    }
    fun updateLabReport(ctx: Context){
        val mapper = jacksonObjectMapper()
        val labreport = labReportsDao.getLabReportsByID(ctx.pathParam("lab-id").toInt())
        if(labreport !=null){
            val updatedlabreport = mapper.readValue<LabReport>(ctx.body())
            if(updatedlabreport !=null){
                ctx.json(labReportsDao.updateLabReport(labreport.id, updatedlabreport))
                ctx.status(200)
            }else{
                ctx.status(404)
            }
        }else{
            ctx.status(404)
        }
    }
    fun deleteLabReport(ctx: Context){
        val labreport = labReportsDao.getLabReportsByID(ctx.pathParam("lab-id").toInt())
        if(labreport !=null){
            labReportsDao.deleteLabReport(ctx.pathParam("lab-id").toInt())
            ctx.status(204)
        }else{
            ctx.status(404)
        }

    }
    //deletes lab reports for specific user
    fun deleteReportByUserID(ctx: Context){
        if(labReportsDao.deleteLabReportByUserId(ctx.pathParam("user-id").toInt())!=0){
            ctx.status(204)
        }else{
            ctx.status(400)
        }
    }
    //gets labreports for specific user
    fun getLabReportByID(ctx: Context){
        val labreport = labReportsDao.getLabReportsByID(ctx.pathParam("lab-id").toInt())
        if(labreport !=null){
            labReportsDao.getLabReportsByID(ctx.pathParam("lab-id").toInt())
            ctx.json(labreport)
            ctx.status(200)
        }else{
            ctx.status(404)
        }
    }
}