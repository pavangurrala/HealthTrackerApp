package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.models.User
import ie.setu.domain.respository.UserDAO
import io.javalin.http.Context



object UserController {
    private val userDAO = UserDAO()
    fun getAllUsers(ctx: Context) {
        ctx.json(userDAO.getAll())
    }
    fun getUserById(ctx: Context) {
        val user = userDAO.getById(ctx.pathParam("user-id").toInt())
        if(user != null){
            ctx.json(user)
        }
    }
    fun getUserByEmail(ctx: Context) {
        val user = userDAO.getuserbyemaiid(ctx.pathParam("email").toString())
        if(user != null){
            ctx.json(user)
        }
    }
    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        if(user != null){
            userDAO.saveUser(user)
            ctx.json(user)
        }
    }
    fun updateUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        if(user != null){
            userDAO.updateUser(user.id, user)
            ctx.json(user)
        }
    }
    fun deleteUser(ctx: Context) {
        val user = userDAO.deleteUser(ctx.pathParam("user-id").toInt())
        if(user != null){
            ctx.status(204)
        }
    }

}