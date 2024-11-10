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
}