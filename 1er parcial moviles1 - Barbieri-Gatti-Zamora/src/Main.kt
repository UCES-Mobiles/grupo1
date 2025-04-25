import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    println("Bienvenido al restaurante Zagaba.")

    // Cargamos un par de clientes
    agregarCliente(Cliente(1, "Juan", "1122334455", "juan@gmail.com"))
    agregarCliente(Cliente(2, "Ana", "1133445566", null))

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

enum class EstadoPedido {
    PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO
}

data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var porcentajeDescuento: Double = 0.0
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
    println("Ingrese su nombre de usuario:")
    val nombre = scanner.nextLine()

    val cliente = buscarClientePorNombre(nombre)
    if (cliente != null) {
        println("¡Bienvenido, ${cliente.nombre}!")
    } else {
        println("Usuario no encontrado. ¿Desea crearlo? (s/n)")
        if (scanner.nextLine().lowercase() == "s") {
            crearUsuario(scanner)
        }
    }
}

// crea repositorios de memoria

object Repositorio {
    val clientes = mutableListOf<Cliente>()
}


// gestion de clientes

fun agregarCliente(cliente: Cliente) {
    Repositorio.clientes.add(cliente)
}

fun buscarClientePorNombre(nombre: String) = Repositorio.clientes.find { it.nombre == nombre }
