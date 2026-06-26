fun main() {
    print("Ingrese el primer numero: ")
    val num1 = readLine()!!.toInt()

    val esPar = if (num1 % 2 == 0) "Par" else "Impar"
    println(esPar)
}
