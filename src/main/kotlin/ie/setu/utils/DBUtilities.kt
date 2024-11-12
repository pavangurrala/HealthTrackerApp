package ie.setu.utils
import ie.setu.domain.db.Activities
import ie.setu.domain.models.User
import ie.setu.domain.db.Users
import ie.setu.domain.models.Activity
import org.jetbrains.exposed.sql.ResultRow


fun mapToUser(it: ResultRow) = User (
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    weight = it[Users.weight],
    height = it[Users.height],
    gender = it[Users.gender]
)
fun mapToActivity(it: ResultRow) = Activity (
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    calories = it[Activities.calories],
    started = it[Activities.started],
    userId = it[Activities.userid]
)