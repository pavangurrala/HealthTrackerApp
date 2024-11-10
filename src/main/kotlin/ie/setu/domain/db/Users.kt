package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object Users : Table(name = "users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 255)
    val height = double("height")
    val weight = double("weight")
    val gender = varchar("gender", 50)
    override val primaryKey = PrimaryKey(id)
}