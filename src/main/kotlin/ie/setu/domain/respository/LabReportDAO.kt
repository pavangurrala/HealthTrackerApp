package ie.setu.domain.respository
import ie.setu.domain.db.Labreports
import ie.setu.domain.db.Users
import ie.setu.domain.models.LabReport
import ie.setu.utils.mapToLabReport
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
class LabReportDAO {
    fun getAll():ArrayList<LabReport>{
        val labReportsList:ArrayList<LabReport> = arrayListOf()
        transaction {
            Labreports.selectAll().map {
                labReportsList.add(mapToLabReport(it))
            }
        }
        return labReportsList
    }

    fun getLabReportsByUserID(id:Int): List<LabReport>{
        return transaction {
            Labreports.selectAll().where{Labreports.patientid eq id}.map { mapToLabReport(it) }
        }
    }
    fun getLabReportsByID(id:Int):LabReport?{
        return transaction {
            Labreports.selectAll().where { Labreports.id eq id }.map{mapToLabReport(it)}.firstOrNull()
        }
    }
    fun addLabReports(labReport: LabReport){
        return transaction{
            Labreports.insert {
                it[reportname] = labReport.reportname
                it[reporttype] = labReport.reporttype
                it[reportsource] = labReport.reportsource
                it[patientid] = labReport.patientid
            }
        }
    }
    fun updateLabReport(id: Int, labReport: LabReport){
        return transaction{
            Labreports.update({ Labreports.id eq id }){
                it[reportname] = labReport.reportname
                it[reporttype] = labReport.reporttype
                it[reportsource] = labReport.reportsource
                it[patientid] = labReport.patientid
            }
        }
    }
    fun deleteLabReport(id: Int){
        return transaction{
            Labreports.deleteWhere { Labreports.id eq id }
        }
    }
    fun deleteLabReportByUserId(userid :Int){
        return transaction{
            Labreports.deleteWhere { Labreports.patientid eq userid }
        }
    }
}