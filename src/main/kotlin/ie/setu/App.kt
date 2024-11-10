package ie.setu
import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig
import org.jetbrains.exposed.sql.Database

fun main(){
    JavalinConfig().startJavalinService()
    DbConfig().getDbConnection()
}