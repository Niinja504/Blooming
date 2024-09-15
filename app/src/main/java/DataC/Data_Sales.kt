package DataC

data class Data_Sales(
    val uuid: String,
    val uuid_Empleado: String,
    val FechaVenta: String,
    val HoraVenta: String,
    val NombreCliente: String,
    val NombreEmpleado: String,
    val TotalVenta: Float,
    val UUID_Producto: String,
    val PrecioProducto: Float,
    val CantidadProducto: Int
)
