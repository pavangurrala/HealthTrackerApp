package ie.setu.domain.respository
import ie.setu.domain.models.Activity
import ie.setu.domain.db.Activities
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

class ActivityDAO {
    //Get all activities in the db
    fun getAll(): ArrayList<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it))
            }
        }
        return activitiesList
    }

    // find specific activity by activity id
    fun findByActivityId(id: Int): Activity? {
        return transaction {
            Activities
                .selectAll().where { Activities.id eq id }
                .map { mapToActivity(it) }
                .firstOrNull()
        }
    }

    //Find all activities for a specific user id
    fun findByUserId(userId: Int): List<Activity> {
        return transaction {
            Activities
                .selectAll().where { Activities.userid eq userId }
                .map { mapToActivity(it) }
        }
    }
        //update the activity by activity id
        fun updateActivity(id: Int,activity: Activity) {
            transaction {
                Activities.update({ Activities.id eq id }) {
                    it[description] = activity.description
                    it[duration] = activity.duration
                    it[started] = activity.started
                    it[calories] = activity.calories
                    it[userid] = activity.userId
                }
            }
        }

        //Save an activity to the database
        fun save(activity: Activity) {
           transaction {
                Activities.insert {
                    it[description] = activity.description
                    it[duration] = activity.duration
                    it[started] = activity.started
                    it[calories] = activity.calories
                    it[userid] = activity.userId
                }
            }
        }
    fun deleteactivity(id: Int) {
        return transaction {
            Activities.deleteWhere { Activities.id eq id }
        }
    }
    fun deleteActivityByUserId (userId: Int): Int{
        return transaction{
            Activities.deleteWhere { Activities.userid eq userId }
        }
    }
}