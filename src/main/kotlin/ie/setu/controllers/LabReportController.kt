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
            }
        }
    }
    fun addLabReport(ctx: Context){
        val mapper = jacksonObjectMapper()
        val labreport = mapper.readValue<LabReport>(ctx.body())
        if(labreport !=null){
            labReportsDao.addLabReports(labreport)
            ctx.json(labreport)
        }
    }
    fun updateLabReport(ctx: Context){
        val mapper = jacksonObjectMapper()
        val labreport = labReportsDao.getLabReportsByID(ctx.pathParam("lab-id").toInt())
        if(labreport !=null){
            val updatedlabreport = mapper.readValue<LabReport>(ctx.body())
            if(updatedlabreport !=null){
                ctx.json(labReportsDao.updateLabReport(labreport.id, updatedlabreport))
            }
        }
    }
    fun deleteLabReport(ctx: Context){
        labReportsDao.deleteLabReport(ctx.pathParam("lab-id").toInt())
        ctx.status(204)
    }
}