package controllers

import models.User

class UserStore {
    private val users = ArrayList<User>()

    fun findAll(): List<User> {
        return users
    }
    fun create(user: User) {
        users.add(user)
    }
    fun findOne(id: Int): User? {
        return users.find {it->it.id == id }
    }
    fun delete (user: User?):Boolean {
        return users.remove(user)
    }
}