package ie.setu.domain.respository

import ie.setu.domain.models.User
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

//import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDAO {
    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it))
            }
        }
        return userList
    }
    fun getById(id: Int): User? {
        return transaction {
            Users.selectAll().where{Users.id eq id}.map { mapToUser(it) }.firstOrNull()
        }
    }
    fun getuserbyemaiid(email: String): User? {
        return transaction{
            Users.selectAll().where{Users.email eq email}.map { mapToUser(it) }.firstOrNull()
        }
    }
    fun saveUser(user: User) {
        return transaction{
            Users.insert {
                it[id] = user.id
                it[name] = user.name
                it[email] = user.email
                it[weight] = user.weight
                it[height] = user.height
                it[gender] = user.gender
            }
        }
    }
    fun updateUser(id: Int, user: User) {
        return transaction{
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[email] = user.email
                it[weight] = user.weight
                it[height] = user.height
                it[gender] = user.gender
            }
        }
    }
    fun deleteUser(id: Int) {
        return transaction{
            Users.deleteWhere { Users.id eq id }
        }
    }
}