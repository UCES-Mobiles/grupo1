import java.util.Scanner

enum class TipoProducto {
    ENTRADA, PLATO_PRINCIPAL, POSTRE, BEBIDA
}

enum class EstadoPedido {
    PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO
}

fun main() {
    val scanner = Scanner(System.`in`)
    println("Bienvenido al restaurante Zagaba...")

    // Ingreso de productos
    agregarProducto(Producto(1, "Empanada", 500.0, 0.0, TipoProducto.ENTRADA))
    agregarProducto(Producto(2, "Milanesa con papas", 2000.0, 0.0, TipoProducto.PLATO_PRINCIPAL))
    agregarProducto(Producto(3, "Helado", 700.0, 0.0, TipoProducto.POSTRE))
    agregarProducto(Producto(4, "Gaseosa", 400.0, 0.0, TipoProducto.BEBIDA))

    // Ingreso de usuarios

    //Admin
    agregarCliente(Cliente(0, "admin","0", "0000000000", "admin@zagaba.com", esAdmin = true))
    //Clientes
    agregarCliente(Cliente(1, "Juan","123", "1122334455", "juan@gmail.com", esAdmin = false))
    agregarCliente(Cliente(2, "Ana","456", "1133445566", null, esAdmin = false))

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

data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var porcentajeDescuento: Double = 0.0,
    var tipo: TipoProducto
) {
    fun precioFinal(): Double = precio * (1 - porcentajeDescuento / 100)
}

data class Cliente(
    val id: Int,
    val nombre: String,
    val cont: String, // contraseña del usuario
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

// Creación de usuario

fun crearUsuario(scanner: Scanner) {
    println("Ingrese su nombre de usuario:")
    val nombre = scanner.nextLine()
    println("Ingrese su contraseña:")
    val cont = scanner.nextLine()
    println("Ingrese su teléfono:")
    val telefono = scanner.nextLine()
    println("Ingrese su email (puede dejarlo vacío):")
    val email = scanner.nextLine().takeIf { it.isNotBlank() }
    val nuevoId = (Repositorio.clientes.maxByOrNull { it.id }?.id ?: 0) + 1
    val nuevoCliente = Cliente(nuevoId, nombre,cont, telefono, email, mutableListOf())
    agregarCliente(nuevoCliente)

    println("Usuario creado exitosamente. Su ID de cliente es: ${nuevoCliente.id}")
}

fun crearUsuarioComoAdmin(scanner: Scanner) {
    println("Ingrese su nombre de usuario:")
    val nombre = scanner.nextLine()
    println("Ingrese su contraseña:")
    val cont = scanner.nextLine()
    println("Ingrese su teléfono:")
    val telefono = scanner.nextLine()
    println("Ingrese su email (puede dejarlo vacío):")
    val email = scanner.nextLine().takeIf { it.isNotBlank() }
    var isAdmin = false
    println("¿Usuario admin? Ingrese (Y) o (N):")
    var isAdminInput = scanner.nextLine()
    while (isAdminInput != "Y" && isAdminInput != "N" && isAdminInput != "y" && isAdminInput != "n" ) {
        println("Entrada inválida. Por favor, ingrese Y o N:")
        isAdminInput = scanner.nextLine().uppercase()
    }
    if (isAdminInput == "Y" || isAdminInput == "y") {
        isAdmin = true
    }

    val nuevoId = (Repositorio.clientes.maxByOrNull { it.id }?.id ?: 0) + 1
    val nuevoCliente = Cliente(nuevoId, nombre, cont, telefono, email, mutableListOf() , isAdmin)
    agregarCliente(nuevoCliente)

    println("Usuario creado exitosamente. Su ID de cliente es: ${nuevoCliente.id}")
}

// Funcion de login
fun logIn(scanner: Scanner) {
    println("Ingrese su nombre de usuario:")
    val nombre = scanner.nextLine()

    println("Ingrese su contraseña:")
    val cont = scanner.nextLine()

    val cliente = Repositorio.clientes.find {
        it.nombre.trim().lowercase() == nombre.trim().lowercase() && it.cont == cont
    }

    if (cliente != null) {
        println("¡Bienvenido, ${cliente.nombre}!")
        if (cliente.esAdmin) {
            menuAdmin(scanner)
        } else {
            menuCliente(scanner, cliente)
        }
    } else {
        println("Nombre de usuario o contraseña incorrectos. ¿Desea crear un nuevo usuario? (s/n)")
        if (scanner.nextLine().lowercase() == "s") {
            crearUsuario(scanner)
        }
    }
}

// --- Funciones de Productos ---

fun agregarProductoMenu(scanner: Scanner) {
    println("\n--- Agregar Producto ---")

    // Nombre
    println("Nombre del producto:")
    val nombre = scanner.nextLine()
    if (nombre.isBlank()) {
        println("El nombre no puede estar vacío.")
        return
    }

    val precio = ingresarPrecioConValidacion(scanner) ?: return
    val tipo = seleccionarTipoProducto(scanner) ?: return
    val descuento = ingresarDescuentoConValidacion(scanner) ?: return
    val nuevoId = (Repositorio.productos.maxByOrNull { it.id }?.id ?: 0) + 1
    agregarProducto(Producto(nuevoId, nombre, precio, descuento, tipo))
    println("✅ Producto agregado exitosamente (ID: $nuevoId)")
}

private fun ingresarPrecioConValidacion(scanner: Scanner): Double? {
    var precio: Double?
    do {
        println("Precio del producto:")
        val input = scanner.nextLine()

        precio = input.toDoubleOrNull()
        if (precio == null) {
            println("⚠️ Error: Ingrese un valor numérico válido.")
            continue
        }

        if (precio < 0) {
            println("⚠️ Advertencia: Está ingresando un valor negativo.")
            println("¿Desea continuar? (Y/N)")
            when (scanner.nextLine().trim().lowercase()) {
                "y" -> return precio
                "n" -> {
                    precio = null
                    continue
                }
                else -> {
                    println("Opción no válida. Volviendo a solicitar precio.")
                    precio = null
                }
            }
        }
    } while (precio == null)

    return precio
}

private fun seleccionarTipoProducto(scanner: Scanner): TipoProducto? {
    while (true) {
        println("\nSeleccione el tipo de producto:")
        TipoProducto.entries.forEachIndexed { index, tipo ->
            println("${index + 1}. ${tipo.name}")
        }
        println("Opción:")

        val opcion = scanner.nextLine().toIntOrNull()
        if (opcion == null) {
            println("⚠️ Error: Ingrese un número válido.")
            continue
        }

        val tipo = TipoProducto.entries.getOrNull(opcion - 1)
        if (tipo != null) {
            return tipo
        } else {
            println("⚠️ Error: Opción no válida. Intente nuevamente.")
        }
    }
}

private fun ingresarDescuentoConValidacion(scanner: Scanner): Double? {
    while (true) {
        println("Porcentaje de descuento (0-100%):")
        val input = scanner.nextLine()

        val descuento = input.toDoubleOrNull()
        if (descuento == null) {
            println("⚠️ Error: Ingrese un valor numérico válido.")
            continue
        }

        when {
            descuento < 0 -> {
                println("⚠️ Error: El descuento no puede ser negativo.")
                continue
            }
            descuento > 100 -> {
                println("⚠️ Error: El descuento no puede ser mayor al 100%.")
                continue
            }
            descuento >= 60 -> {
                println("⚠️ Advertencia: Está ingresando un descuento mayor al 60%.")
                println("¿Desea continuar? (Y/N)")
                when (scanner.nextLine().trim().lowercase()) {
                    "y" -> return descuento
                    "n" -> continue
                    else -> {
                        println("Opción no válida. Volviendo a solicitar descuento.")
                        continue
                    }
                }
            }
            else -> return descuento
        }
    }
}

fun buscarProductosMenu(scanner: Scanner): Producto? {
    while (true) {
        println(
            """
            |--- Buscar Producto ---
            |1. Listar todos
            |2. Buscar por ID
            |3. Buscar por nombre
            |4. Buscar por tipo
            |5. Buscar por precio máximo
            |6. Cancelar
            |Seleccione opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> {
                if (Repositorio.productos.isEmpty()) {
                    println("No hay productos registrados.")
                    return null
                }
                Repositorio.productos.sortedBy { it.id }.forEachIndexed { index, producto ->
                    println("${index + 1}. ${producto.toString()}")
                }
                println("Ingrese el número del producto:")
                val index = scanner.nextLine().toIntOrNull()?.minus(1) ?: return null
                return Repositorio.productos.getOrNull(index)
            }
            "2" -> {
                println("Ingrese ID del producto:")
                val id = scanner.nextLine().toIntOrNull()
                return if (id == null) {
                    println("ID inválido")
                    null
                } else {
                    Repositorio.productos.find { it.id == id }.also {
                        if (it == null) println("Producto no encontrado")
                    }
                }
            }
            "3" -> {
                println("Ingrese nombre o parte del nombre:")
                val nombre = scanner.nextLine()
                val resultados = Repositorio.productos.filter {
                    it.nombre.contains(nombre, ignoreCase = true)
                }
                if (resultados.isEmpty()) {
                    println("No se encontraron productos")
                    return null
                }
                resultados.forEachIndexed { index, producto ->
                    println("${index + 1}. ${producto.toString()}")
                }
                println("Ingrese el número del producto:")
                val index = scanner.nextLine().toIntOrNull()?.minus(1) ?: return null
                return resultados.getOrNull(index)
            }
            "4" -> {
                println("Tipos disponibles: ${TipoProducto.entries.joinToString()}")
                println("Ingrese tipo:")
                val tipo = try {
                    TipoProducto.valueOf(scanner.nextLine().uppercase())
                } catch (e: Exception) {
                    println("Tipo inválido")
                    return null
                }
                val resultados = Repositorio.productos.filter { it.tipo == tipo }
                if (resultados.isEmpty()) {
                    println("No hay productos de este tipo")
                    return null
                }
                resultados.forEachIndexed { index, producto ->
                    println("${index + 1}. ${producto.toString()}")
                }
                println("Ingrese el número del producto:")
                val index = scanner.nextLine().toIntOrNull()?.minus(1) ?: return null
                return resultados.getOrNull(index)
            }
            "5" -> {
                println("Ingrese precio máximo:")
                val precioMax = scanner.nextLine().toDoubleOrNull()
                if (precioMax == null) {
                    println("Precio inválido")
                    return null
                }
                val resultados = Repositorio.productos.filter { it.precio <= precioMax }
                    .sortedBy { it.precio }
                if (resultados.isEmpty()) {
                    println("No hay productos en este rango de precio")
                    return null
                }
                resultados.forEachIndexed { index, producto ->
                    println("${index + 1}. ${producto.toString()}")
                }
                println("Ingrese el número del producto:")
                val index = scanner.nextLine().toIntOrNull()?.minus(1) ?: return null
                return resultados.getOrNull(index)
            }
            "6" -> return null
            else -> println("Opción inválida")
        }
    }
}

fun modificarProductoMenu(scanner: Scanner) {
    println("\n--- Modificar Producto ---")
    val productoOriginal = buscarProductosMenu(scanner) ?: return

    // Creamos una copia mutable para los cambios
    val cambios = productoOriginal.copy()
    var cambiosRealizados = false

    while (true) {
        println(
            """
            |Producto seleccionado: ${cambios.nombre} (ID: ${cambios.id})
            |1. Cambiar nombre (Actual: ${cambios.nombre})
            |2. Cambiar precio (Actual: ${cambios.precio})
            |3. Cambiar descuento (Actual: ${cambios.porcentajeDescuento}%)
            |4. Cambiar tipo (Actual: ${cambios.tipo})
            |5. Guardar cambios
            |6. Cancelar
            |Seleccione opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> {
                println("Nuevo nombre:")
                val inputNombre = scanner.nextLine()
                if (inputNombre.isBlank()) {
                    println("El nombre no puede estar vacío.")
                } else {
                    cambios.nombre = inputNombre
                    cambiosRealizados = true
                    println("✅ Nombre actualizado (pendiente de guardar)")
                }
            }
            "2" -> {
                val nuevoPrecio = ingresarPrecioConValidacion(scanner)
                if (nuevoPrecio != null) {
                    cambios.precio = nuevoPrecio
                    cambiosRealizados = true
                    println("✅ Precio actualizado (pendiente de guardar)")
                }
            }
            "3" -> {
                val nuevoDescuento = ingresarDescuentoConValidacion(scanner)
                if (nuevoDescuento != null) {
                    cambios.porcentajeDescuento = nuevoDescuento
                    cambiosRealizados = true
                    println("✅ Descuento actualizado (pendiente de guardar)")
                }
            }
            "4" -> {
                val nuevoTipo = seleccionarTipoProducto(scanner)
                if (nuevoTipo != null) {
                    cambios.tipo = nuevoTipo
                    cambiosRealizados = true
                    println("✅ Tipo actualizado (pendiente de guardar)")
                }
            }
            "5" -> {
                if (cambiosRealizados) {
                    // Reemplazamos el producto original con los cambios
                    val index = Repositorio.productos.indexOfFirst { it.id == productoOriginal.id }
                    if (index != -1) {
                        Repositorio.productos[index] = cambios
                        println("✅ Cambios guardados exitosamente")
                    } else {
                        println("⚠️ Error: Producto no encontrado en el repositorio")
                    }
                } else {
                    println("ℹ️ No se realizaron cambios")
                }
                return
            }
            "6" -> {
                println("⚠️ Modificación cancelada - No se guardaron los cambios")
                return
            }
            else -> println("⚠️ Opción inválida")
        }
    }
}

fun eliminarProductoMenu(scanner: Scanner) {
    println("\n--- Eliminar Producto ---")
    val producto = buscarProductosMenu(scanner) ?: return

    println("¿Está seguro que desea eliminar el producto ${producto.nombre} (ID: ${producto.id})? (s/n)")
    when (scanner.nextLine().lowercase()) {
        "y" -> {
            Repositorio.productos.remove(producto)
            println("Producto eliminado exitosamente")
        }
        else -> println("Operación cancelada")
    }
}

fun agregarProducto(producto: Producto) {
    if (Repositorio.productos.any { it.id == producto.id }) {
        println("Error: ya existe un producto con ID ${producto.id}")
        return
    }
    Repositorio.productos.add(producto)
}

//Menú Admin:

fun menuAdmin(scanner: Scanner) {
    while (true) {
        println(
            """
            |--- Administración Zagaba ---
            |1. Agregar Producto
            |2. Modificar Producto
            |3. Ver Productos
            |4. Borrar Producto
            |5. Ver Clientes
            |6. Buscar Cliente
            |7. Eliminar Cliente
            |8. Agregar usuario admin
            |9. Generar reportes
            |10. Volver al menú principal
            |Seleccione una opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> agregarProductoMenu(scanner)
            "2" -> modificarProductoMenu(scanner)
            "3" -> {
                println("\n--- Listado de Productos ---")
                if (Repositorio.productos.isEmpty()) {
                    println("No hay productos registrados")
                } else {
                    Repositorio.productos.sortedBy { it.id }.forEach { println(it) }
                }
            }
            "4" -> eliminarProductoMenu(scanner)
            "5" -> {
                println("\n--- Listado de Clientes ---")
                if (Repositorio.clientes.isEmpty()) {
                    println("No hay clientes registrados")
                } else {
                    Repositorio.clientes.forEach { println(it) }
                }
            }
            "6" -> {
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
            "7" -> {
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
            "8" -> {crearUsuarioComoAdmin(scanner) }
            "9" -> {generarReportes(scanner)}
            "10" -> return
            else -> println("Opción inválida")
        }
    }
}

// Menu cliente

fun menuCliente(scanner: Scanner, cliente: Cliente) {
    while (true) {
        println(
            """
            |--- Menú Cliente ---
            |1. Hacer un nuevo pedido
            |2. Ver mis pedidos
            |3. Cancelar un pedido
            |4. Cerrar sesión
            |Seleccione una opción:
            """.trimMargin()
        )

        when (scanner.nextLine().trim()) {
            "1" -> hacerPedido(scanner, cliente)
            "2" -> {
                if (cliente.pedidos.isEmpty()) {
                    println("No tiene pedidos registrados.")
                } else {
                    println("Pedidos realizados:")
                    cliente.pedidos.sortedBy { it.fecha }.forEach { println(it) }
                }
            }
            "3" -> cancelarPedido(scanner, cliente)
            "4" -> {
                println("Sesión cerrada. Hasta luego, ${cliente.nombre}.")
                return
            }
            else -> println("Opción inválida.")
        }
    }
}

// funcion para hacer los pedidos

fun hacerPedido(scanner: Scanner, cliente: Cliente) {
    println("Productos disponibles:")
    Repositorio.productos.forEach { println("${it.id}. ${it.nombre} - \$${it.precioFinal()} (${it.tipo})") }

    println("Ingrese los IDs de los productos que desea (separados por coma):")
    val ids = scanner.nextLine()
        .split(",")
        .mapNotNull { it.trim().toIntOrNull() }

    if (ids.isEmpty()) {
        println("No se seleccionó ningún producto válido.")
        return
    }

    val fecha = java.time.LocalDate.now().toString()
    try {
        val pedido = tomarPedido(cliente.id, ids, fecha)
        println("Pedido creado con éxito:\n$pedido")
    } catch (e: Exception) {
        println("Error al crear el pedido: ${e.message}")
    }
}

// funciones para cancelar un pedido

fun Pedido.cancelar() {
    if (estado == EstadoPedido.ENTREGADO || estado == EstadoPedido.CANCELADO) {
        throw IllegalStateException("El pedido ya fue ${estado.name.lowercase()}, no se puede cancelar.")
    }
    estado = EstadoPedido.CANCELADO
}

fun cancelarPedido(scanner: Scanner, cliente: Cliente) {
    val pedidosActivos = cliente.pedidos.filter { it.estado != EstadoPedido.ENTREGADO && it.estado != EstadoPedido.CANCELADO }

    if (pedidosActivos.isEmpty()) {
        println("No tiene pedidos activos para cancelar.")
        return
    }

    println("Pedidos activos:")
    pedidosActivos.forEach { println("ID: ${it.id} - ${it.productos.joinToString { p -> p.nombre }} (Estado: ${it.estado})") }

    println("Ingrese el ID del pedido que desea cancelar:")
    val id = scanner.nextLine().toIntOrNull()

    val pedido = pedidosActivos.find { it.id == id }
    if (pedido != null) {
        try {
            pedido.cancelar()
            println("Pedido cancelado exitosamente.")
        } catch (e: Exception) {
            println("No se pudo cancelar el pedido: ${e.message}")
        }
    } else {
        println("Pedido no encontrado o ya fue entregado/cancelado.")
    }
}

fun tomarPedido(clienteId: Int, productosIds: List<Int>, fecha: String): Pedido {
    val cliente = Repositorio.clientes.find { it.id == clienteId } ?: throw IllegalArgumentException("Cliente no encontrado")
    val productos = productosIds.map { id ->
        Repositorio.productos.find { it.id == id } ?: throw IllegalArgumentException("Producto $id no existe")
    }
    val pedido = Pedido(id = Repositorio.pedidos.size + 1, cliente = cliente, productos = productos, fecha = fecha)
    cliente.pedidos.add(pedido)
    Repositorio.pedidos.add(pedido)
    return pedido
}

//Crea repositorios de memoria

object Repositorio {
    val clientes = mutableListOf<Cliente>()
    val productos = mutableListOf<Producto>()
    val pedidos = mutableListOf<Pedido>()
}

//Gestion de clientes

fun agregarCliente(cliente: Cliente) {
    Repositorio.clientes.add(cliente)
}

fun eliminarCliente(cliente: Cliente) {
    Repositorio.clientes.remove(cliente)
}

// funciones para generar reportes

fun generarReportes(scanner: Scanner) {
    println(
        """
            |--- Reportes del negocio ---
            |1. Ver los pedidos de un cliente
            |2. Ver clientes con varios pedidos
            |3. Total recaudado
            |Seleccione una opción:
            """.trimMargin()
    )

    when (scanner.nextLine().trim()) {
        "1" -> {
            while (true) {
                println("Ingrese el ID del cliente:")
                val idInput = scanner.nextLine()
                val id = idInput.toIntOrNull()

                if (id == null) {
                    println("ID inválido.")
                    continue
                }

                val clienteBuscado = Repositorio.clientes.find { it.id == id }
                if (clienteBuscado != null) {
                    if (Repositorio.pedidos.find { it.id == id } != null) {
                        println("Los pedidos de ${clienteBuscado.nombre} fueron: \n")
                        pedidosPorCliente(clienteBuscado.id)
                    } else {
                        println("Ese usuario no tiene pedidos realizados.")
                        return
                    }
                }   else {
                    println("Usuario no encontrado.")
                    return
                }
            }
        }

        "2" -> {
            if (Repositorio.pedidos.isEmpty()) {
                println("No hubieron pedidos realizados hasta el momento.")
                return
            } else {
                println("Clientes con múltiples pedidos:\n")
                clientesConMultiplesPedidos()
                return
            }
        }
        "3" -> {
            if (Repositorio.pedidos.isEmpty()) {
                println("No hubieron pedidos entregados hasta el momento.")
                return
            } else {
                println("Total recaudado: ")
                totalRecaudado()
                return
            }
        }
        else -> println("Opción inválida.")
    }
}

fun pedidosPorCliente(clienteId: Int) =
    Repositorio.pedidos.filter { it.cliente.id == clienteId }.sortedBy { it.fecha }

fun clientesConMultiplesPedidos() =
    Repositorio.clientes.filter { it.pedidos.size > 1 }

fun totalRecaudado() =
    Repositorio.pedidos.filter { it.estado == EstadoPedido.ENTREGADO }
        .sumOf { it.montoTotal }