import java.util.Scanner

data class Producto(
    val nombre: String,
    val cantidad: Int,
    val precio: Double
) {
    override fun toString(): String {
        return "Nombre: $nombre | Cantidad: $cantidad | Precio: $${String.format("%.2f", precio)}"
    }
}

class Inventario {
    private val listaProductos = mutableListOf<Producto>()


    fun agregarProducto(producto: Producto) {
        listaProductos.add(producto)
        println("¡Producto '${producto.nombre}' agregado con éxito!")
    }


    fun mostrarInventario() {
        if (listaProductos.isEmpty()) {
            println("El inventario está vacío.")
            return
        }
        println("\n--- LISTA DE PRODUCTOS ---")
        listaProductos.forEach { println(it) }
    }


    fun buscarPorNombre(nombreBuscar: String) {
        val encontrados = listaProductos.filter {
            it.nombre.contains(nombreBuscar, ignoreCase = true)
        }

        if (encontrados.isEmpty()) {
            println("No se encontraron productos con el nombre: $nombreBuscar")
        } else {
            println("\n--- PRODUCTOS ENCONTRADOS ---")
            encontrados.forEach { println(it) }
        }
    }
}


fun main() {
    val inventario = Inventario()
    val scanner = Scanner(System.`in`)
    var opcion: Int

    do {
        println("\n=== GESTIÓN DE INVENTARIO ===")
        println("1. Agregar producto")
        println("2. Mostrar inventario")
        println("3. Buscar producto")
        println("4. Salir")
        print("Seleccione una opción (1-4): ")

        opcion = scanner.nextIntOrNull() ?: 0

        when (opcion) {
            1 -> {
                print("Ingrese el nombre del producto: ")
                val nombre = scanner.next()

                print("Ingrese la cantidad: ")
                val cantidad = scanner.nextIntOrNull() ?: 0

                print("Ingrese el precio: ")
                val precio = scanner.nextDoubleOrNull() ?: 0.0

                val nuevoProducto = Producto(nombre, cantidad, precio)
                inventario.agregarProducto(nuevoProducto)
            }
            2 -> {
                inventario.mostrarInventario()
            }
            3 -> {
                print("Ingrese el nombre del producto a buscar: ")
                val nombreBuscar = scanner.next()
                inventario.buscarPorNombre(nombreBuscar)
            }
            4 -> {
                println("Saliendo del sistema... ¡Hasta luego!")
            }
            else -> {
                println("Opción no válida. Intente de nuevo.")
            }
        }
    } while (opcion != 4)
}


fun Scanner.nextIntOrNull(): Int? {
    return try {
        this.nextInt()
    } catch (e: Exception) {
        this.next()
        null
    }
}

fun Scanner.nextDoubleOrNull(): Double? {
    return try {
        this.nextDouble()
    } catch (e: Exception) {
        this.next()
        null
    }
}
