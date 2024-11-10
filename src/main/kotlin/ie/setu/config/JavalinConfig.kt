package ie.setu.config

import io.javalin.Javalin
import ie.setu.controllers.UserController

class JavalinConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create().apply {
            exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404-Not Found") }
        }
            .start(7001)
        registerRoutes(app)
        return app

    }

    private fun registerRoutes(app: Javalin) {
        app.get("/api/users", UserController::getAllUsers)
    }
}