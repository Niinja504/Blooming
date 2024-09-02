package DataC

data class DataOrdersDelivered_Admin(
    val uuid: String,
    val uuid_Cliente: String,
    val FechaVenta: String,
    val HoraVenta: String,
    val SubTotal: Float,
    //
    val UUID_Producto: String,
    val PrecioProducto: Float,
    val CantidadProducto: Int,
    //
    val FechaEntrega: String,
    val HorarioEntrega : String,
    //
    val NombreCliente: String,
    val NombreCalle: String,
    val LugarEntrega: String,
    val Colonia: String,
    val Coordenadas: String,
    //
    val SinMensaje: String,
    val Dedicatoria: String,
    val EnvioSinNombre: String,
    val NombreEmisor: String
)
