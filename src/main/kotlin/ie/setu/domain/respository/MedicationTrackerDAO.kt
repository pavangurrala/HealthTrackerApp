package ie.setu.domain.respository
import ie.setu.domain.db.MedicationTracker
import ie.setu.domain.models.MedicationTracking
import ie.setu.utils.mapToMedicationTracker
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
class MedicationTrackerDAO {
    //gets all medication tracking records
    fun getAll():ArrayList<MedicationTracking>{
        val medicationTrackerList : ArrayList<MedicationTracking> = arrayListOf()
        transaction {
            MedicationTracker.selectAll().map {
                medicationTrackerList.add(mapToMedicationTracker(it))
            }
        }
        return medicationTrackerList
    }
    //gets the medication record by user id
    fun getMedicationTrackerByUserID(id:Int):List<MedicationTracking>{
        return transaction { MedicationTracker.selectAll().where {MedicationTracker.patientid eq id }.map {
            mapToMedicationTracker(it)
        }
        }
    }
    // gets medication details by id
    fun getMedicationTrackerById(id:Int):MedicationTracking?{
        return transaction {
            MedicationTracker.selectAll().where{MedicationTracker.id eq id}.map { mapToMedicationTracker(it) }.firstOrNull()
        }
    }
    //add a new medication record against the userID
    fun save(medicationTracking: MedicationTracking){
        transaction {
            MedicationTracker.insert {
                it[medicinename] = medicationTracking.medicinename
                it[datetimeofintake] = medicationTracking.datetimeofintake
                it[dosage] = medicationTracking.dosage
                it[medtakenornot] = medicationTracking.medtakenornot
                it[patientid] = medicationTracking.patientid

            }
        }
    }
    //updates the existing medication record by ID
    fun updateMedicationTracking(id: Int, medicationTracking: MedicationTracking){
        return transaction {
            MedicationTracker.update({ MedicationTracker.id eq id }) {
                it[medicinename] = medicationTracking.medicinename
                it[datetimeofintake] = medicationTracking.datetimeofintake
                it[dosage] = medicationTracking.dosage
                it[medtakenornot] = medicationTracking.medtakenornot
                it[patientid] = medicationTracking.patientid
            }

        }
    }
    //deletes the existing medication record
    fun deleteMedicationTracking(id: Int){
        return transaction {
            MedicationTracker.deleteWhere { MedicationTracker.id eq id }
        }
    }
    //deletes the existing medication record with specific user ID
    fun deleteMedicationTrackingByUserID(userID:Int){
        return transaction {
            MedicationTracker.deleteWhere { MedicationTracker.patientid eq userID }
        }
    }

}