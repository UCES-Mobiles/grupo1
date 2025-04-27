# grupo1

---------------------------------------------------------------------------------

Barbieri, Mariano: Gestión de clientes, alta, baja y búsqueda de clientes desde  menú admin.
Realización de pedidos, cambio de estado de los mismos, gestión de descuentos,  reportes de compras y login.

Gatti, Gaspar: Gestión de productos, alta, baja, búsqueda y modificación de productos. Y ampliación de Readme.

Zamora, Damian Pablo: Gestión ppal de proyecto Git, Gestión de pedidos, alta, baja y cancelación de los  mismos.

---------------------------------------------------------------------------------

Consigna de Examen Grupal – Sistema de Pedidos para Restaurante

**Restaurante Zagaba - Sistema de Gestión**

Restaurante Zagaba es un sistema de gestión de pedidos pensado para administrar productos, clientes y órdenes en un restaurante.
Permite tanto la operación de clientes como de administradores, e incluye:

* Gestión de productos (alta, baja, modificación, búsqueda).
* Gestión de clientes.
* Toma y cancelación de pedidos.
* Reportes de ventas.
* Sistema de descuentos automático por compras.

Manual de uso:

Menú Principal
1. Log in: Ingresa como cliente o administrador.
2. Crear Usuario: Registra un nuevo cliente.
3. Salir: Cierra el sistema.

Funcionalidades de Cliente:

Una vez logueado como cliente obtendrá las siguientes funcionalidades:

* Realizar un nuevo pedido (con selección de productos).
* Ver historial de pedidos realizados.
* Cancelar un pedido activo.
* Cerrar sesión y volver al menú principal.

Aplicación de descuentos automáticos bajo cantidad de pedidos:

Cada 3er pedido, el cliente recibe automáticamente un 10% de descuento en su compra.

Funcionalidades de Administrador:

Opción	Funcionalidad

* Agregar un nuevo producto al menú.
* Modificar un producto existente.
* Ver listado completo de productos.
* Eliminar un producto.
* Ver listado completo de clientes.
* Buscar un cliente por ID.
* Eliminar un cliente (no se puede eliminar un administrador).
* Crear un nuevo usuario administrador.
* Generar reportes de pedidos y ventas.
* Cambiar el estado de un pedido (avance en la preparación y entrega).

Sistema de Reportes:

Desde el menú de reportes, el administrador puede:

* Ver los pedidos de un cliente específico.
* Ver clientes que hayan realizado múltiples pedidos.
* Ver el total recaudado por producto (cantidad vendida y monto total) y el total general.

Estructura principal del sistema:

* Productos: Productos ofrecidos por el restaurante.
* Cliente: Datos del usuario que realiza pedidos.
* Pedido: Pedido realizado (con estado y productos asociados).

