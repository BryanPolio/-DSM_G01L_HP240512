fun main() {
    var opcion: Int
    do {
        println("")
        println("----- MENU -----")
        println("1. Sumar dos numeros")
        println("2. Salir")
        print("Elija una opcion: ")
        opcion = readLine()!!.toInt()
        when (opcion) {
            1 -> {
                print("Ingrese el primer numero: ")
                val num1 = readLine()!!.toInt()
                print("Ingrese el segundo numero: ")
                val num2 = readLine()!!.toInt()
                println("Suma: ${num1 + num2}")
                println("Resta: ${num1 - num2}")
                println("Producto: ${num1 * num2}")
                println("Division: ${num1 / num2}")
            }
            2 -> println("Saliendo del programa...")
            else -> println("Opcion no valida")
        }
    } while (opcion != 2)
}
