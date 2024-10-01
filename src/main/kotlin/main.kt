import controllers.UserStore
import models.User

//var user = User()
val userStore = UserStore()

fun main() {

    //main
    println("Welcome to Health Tracker")
    //addUser()
    //listUser()
    runApp()
}
fun addUser(){
    val user = User()
    println("Please enter the following for the username:")
    print("    Name: ")
    user.name = readln()
    print("    Email: ")
    user.email = readln()
    print("    Id:    ")
    user.id = readln()?.toIntOrNull()?:-1
    print("    Height: ")
    user.height = readln()?.toFloatOrNull()?:-1.0f
    print("    Weight: ")
    user.weight = readln()?.toDoubleOrNull()?:-1.0
    print("    Gender: ")
    user.Gender = gender(readln().toString())
    userStore.create(user)

}

fun getUserById() : User?{
    println("Please enter the ID for the user:")
    return userStore.findOne(readln().toString()?.toIntOrNull()?:-1)
}
fun searchById(){
    val user = getUserById()
    if (user != null){
        println(user)
    }else println("No user found")
}

fun deleteUser(){
    if (userStore.delete(getUserById()!!)){
        println("User deleted")
    }else println("User not found")
}
fun listUser(){
    print("The user details are: ${userStore.findAll()}")
}
fun menu():Int{
    println("\nMain Menu:")
    println("1. Add User")
    println("2. List User")
    println("3. Search By ID")
    println("4. Delete User")
    println("0. Exit")
    print("Please enter your option: ")
    return readlnOrNull()?.toIntOrNull() ?: -1
}

fun runApp(){
    var input: Int
    do{
        input = menu()
        when (input) {
            1 -> addUser()
            2 -> listUser()
            3 -> searchById()
            4 -> deleteUser()
            in(5..6) -> println("Feature coming soon")
            0 -> println("Bye...")
            else -> println("Invalid Option")
        }
    }while (input != 0)
}

fun gender(g:String) : String {
   if (g == "M") {
        return "Male"

    }else if (g == "F") {
        return "Female"
    }
    else{
        return "Not Valid Input"
    }

}