import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    println("Bienvenido al restaurante Zagaba.")

    // Cargamos algunos productos al menú
    agregarProducto(Producto(1, "Empanada", 500.0, 0.0, TipoProducto.ENTRADA))
    agregarProducto(Producto(2, "Milanesa con papas", 2000.0, 0.0, TipoProducto.PLATO_PRINCIPAL))
    agregarProducto(Producto(3, "Helado", 700.0, 0.0, TipoProducto.POSTRE))
    agregarProducto(Producto(4, "Gaseosa", 400.0, 0.0, TipoProducto.BEBIDA))

    // Cargamos un par de clientes
    agregarCliente(Cliente(1, "Juan Pérez", "1122334455", "juan@gmail.com"))
    agregarCliente(Cliente(2, "Ana López", "1133445566", null))

    Repositorio.clientes.add(
        Cliente(
            id = 0,
            nombre = "admin",
            telefono = "0000000000",
            email = "admin@zagaba.com",
            esAdmin = true
        )
    )

    while (true) {
        println(
            """
            |--- Menú Principal ---
            |1. Log in
            |2. Crear Usuario
            |3. Salir
            |Seleccione una opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> logIn(scanner)
            "2" -> crearUsuario(scanner)
            "3" -> {
                println("Gracias por visitar el restaurante Zagaba. ¡Hasta pronto!")
                return
            }
            else -> println("Opción inválida. Por favor intente nuevamente.")
        }
    }
}

enum class TipoProducto {
    ENTRADA, PLATO_PRINCIPAL, POSTRE, BEBIDA
}

enum class EstadoPedido {
    PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO
}

data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var porcentajeDescuento: Double = 0.0,
    val tipo: TipoProducto
) {
    fun precioFinal(): Double = precio * (1 - porcentajeDescuento / 100)
}

data class Cliente(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val email: String?,
    val pedidos: MutableList<Pedido> = mutableListOf(),
    val esAdmin: Boolean = false
)

data class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val fecha: String,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE
) {
    val montoTotal: Double get() = productos.sumOf { it.precioFinal() }

    fun avanzarEstado() {
        estado = when (estado) {
            EstadoPedido.PENDIENTE -> EstadoPedido.EN_PREPARACION
            EstadoPedido.EN_PREPARACION -> EstadoPedido.ENVIADO
            EstadoPedido.ENVIADO -> EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO -> throw IllegalStateException("No se puede avanzar más desde $estado")
        }
    }

    override fun toString(): String {
        return "Pedido(id=$id, cliente=${cliente.nombre}, productos=${productos.map { it.nombre }}, fecha='$fecha', estado=$estado, montoTotal=$montoTotal)"
    }
}

// creación de usuario

fun crearUsuario(scanner: Scanner) {
    println("Ingrese su nombre completo:")
    val nombre = scanner.nextLine()
    println("Ingrese su teléfono:")
    val telefono = scanner.nextLine()
    println("Ingrese su email (puede dejarlo vacío):")
    val email = scanner.nextLine().takeIf { it.isNotBlank() }

    val nuevoId = (Repositorio.clientes.maxByOrNull { it.id }?.id ?: 0) + 1
    val nuevoCliente = Cliente(nuevoId, nombre, telefono, email)
    agregarCliente(nuevoCliente)

    println("Usuario creado exitosamente. Su ID de cliente es: ${nuevoCliente.id}")
}

// funcion de login

fun logIn(scanner: Scanner) {
    println("Ingrese su id de usuario:")
    val idInput = scanner.nextLine()
    val id = idInput.toIntOrNull()
    if (id == null) {
        println("ID inválido.")
    } else {
        val cliente = buscarClientePorId(id)
        if (cliente != null) {
            println("¡Bienvenido, ${cliente.nombre}!")
            if (cliente.esAdmin) {
                menuAdmin(scanner)
            } else {
                // -- FALTA HACER MENÚ CLIENTE --
            }
        } else {
            println("Usuario no encontrado. ¿Desea crearlo? (s/n)")
            if (scanner.nextLine().lowercase() == "s") {
                crearUsuario(scanner)
            }
        }
    }

}

// Menú de Admin:

fun menuAdmin(scanner: Scanner) {
    while (true) {
        println(
            """
            |--- Menú Admin ---
            |1. Agregar Producto
            |2. Modificar Producto
            |3. Ver Productos
            |4. Ver Clientes
            |5. Buscar Cliente
            |6. Eliminar Cliente
            |7. Volver al menú principal
            |Seleccione una opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> {
                println("Nombre del producto:")
                val nombre = scanner.nextLine()

                println("Precio:")
                val precioInput = scanner.nextLine()
                val precio = precioInput.toDoubleOrNull()
                if (precio == null) {
                    println("Precio inválido.")
                    continue
                }

                println("Tipo (ENTRADA, PLATO_PRINCIPAL, POSTRE, BEBIDA):")
                val tipoInput = scanner.nextLine()
                val tipo = try {
                    TipoProducto.valueOf(tipoInput.uppercase())
                } catch (e: Exception) {
                    println("Tipo de producto inválido.")
                    continue
                }

                println("Porcentaje de descuento (0 si no tiene):")
                val descuentoInput = scanner.nextLine()
                val descuento = descuentoInput.toDoubleOrNull() ?: 0.0

                val nuevoId = (Repositorio.productos.maxByOrNull { it.id }?.id ?: 0) + 1
                agregarProducto(Producto(nuevoId, nombre, precio, descuento, tipo))
                println("Producto agregado exitosamente.")
            }
            "2" -> {
                println("ID del producto a modificar:")
                val idInput = scanner.nextLine()
                val id = idInput.toIntOrNull()
                if (id == null) {
                    println("ID inválido.")
                    continue
                }

                println("Nuevo nombre:")
                val nuevoNombre = scanner.nextLine()

                println("Nuevo precio:")
                val nuevoPrecioInput = scanner.nextLine()
                val nuevoPrecio = nuevoPrecioInput.toDoubleOrNull()
                if (nuevoPrecio == null) {
                    println("Precio inválido.")
                    continue
                }

                modificarProducto(id, nuevoNombre, nuevoPrecio)
            }
            "3" -> {
                println("Productos actuales:")
                Repositorio.productos.forEach { println(it) }
            }
            "4" -> {
                println("Clientes actuales:")
                Repositorio.clientes.forEach { println(it) }
            }
            "5" -> {
                println("Ingrese el ID del cliente:")
                val idInput = scanner.nextLine()
                val id = idInput.toIntOrNull()
                if (id == null) {
                    println("ID inválido.")
                    continue
                }

                val clienteBuscado = Repositorio.clientes.find { it.id == id }
                if (clienteBuscado != null) {
                    println("Nombre del cliente: ${clienteBuscado.nombre}")
                    println("mail: ${clienteBuscado.email}")
                    println("Teléfono: ${clienteBuscado.telefono}")
                    println("Pedidos: ${clienteBuscado.pedidos}")
                } else {
                    println("Usuario no encontrado. ¿Desea crearlo? (s/n)")
                    val confirmacion = scanner.nextLine().lowercase()
                    if (confirmacion == "s") {
                        crearUsuario(scanner)
                    }
                }
            }
            "6" -> {
                println("Ingrese el ID del cliente a eliminar:")
                val idInput = scanner.nextLine()
                val id = idInput.toIntOrNull()
                if (id == null) {
                    println("ID inválido.")
                    continue
                }

                val clienteBuscado = Repositorio.clientes.find { it.id == id }
                if (clienteBuscado != null) {
                    println("El cliente eliminar es: ${clienteBuscado.nombre} de id= ${clienteBuscado.id}")
                    println("¿Está seguro que quiere eliminarlo? (s/n)")
                    val confirmacion = scanner.nextLine().lowercase()
                    if (confirmacion == "s") {
                        eliminarCliente(clienteBuscado)
                        println("Cliente eliminado exitosamente.")
                    } else {
                        println("Operación cancelada.")
                    }
                } else {
                    println("Usuario no encontrado.")
                }
            }
            "7" -> return
            else -> println("Opción inválida.")
        }
    }
}
// Funciones de Gestiones de Productos:

fun modificarProducto(id: Int, nuevoNombre: String, nuevoPrecio: Double) {
    val prod = Repositorio.productos.find { it.id == id }
    if (prod == null) {
        println("No se encontró el producto con ID $id")
    } else {
        prod.nombre = nuevoNombre
        prod.precio = nuevoPrecio
        println("Producto con ID $id modificado exitosamente.")
    }
}

// crea repositorios de memoria

object Repositorio {
    val clientes = mutableListOf<Cliente>()
    val productos = mutableListOf<Producto>()
}

fun agregarProducto(producto: Producto) {
    if (Repositorio.productos.any { it.id == producto.id }) {
        println("Error: ya existe un producto con ID ${producto.id}")
        return
    }
    Repositorio.productos.add(producto)
}

// gestion de clientes

fun agregarCliente(cliente: Cliente) {
    Repositorio.clientes.add(cliente)
}

fun buscarClientePorId(id: Int) = Repositorio.clientes.find { it.id == id }


fun eliminarCliente(cliente: Cliente) {
    Repositorio.clientes.remove(cliente)
}
