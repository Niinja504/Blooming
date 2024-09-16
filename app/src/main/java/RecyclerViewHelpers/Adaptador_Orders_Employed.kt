package RecyclerViewHelpers

import DataC.Data_Orders
import DataC.Data_Productos
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import java.sql.SQLException

class Adaptador_Orders_Employed (var Datos: List<Data_Orders>): RecyclerView.Adapter<ViewHolder_Orders_Employed>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Orders_Employed {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_orders_employed, parent, false)
        return  ViewHolder_Orders_Employed(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Orders_Employed, posicion: Int){
        val item = Datos[posicion]
        holder.CostoPedido.text = item.SubTotal.toString()
        holder.FechaEntrega.text = item.FechaEntrega
        holder.HoraEntrega.text = item.HorarioEntrega
        holder.NombreCliente.text = item.NombreCliente
        holder.Colonia.text = item.Colonia
        holder.Calle.text = item.NombreCalle


        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val Productos = obtenerProductosParaPedido(item.uuid)
                val bundle = Bundle().apply {
                    putString("uuid", item.uuid)
                    putString("HoraVenta", item.HoraVenta)
                    putFloat("SubTotal", item.SubTotal)
                    putString("Pendiente", item.Pendiente)
                    putString("UUID_Producto", item.UUID_Producto)
                    putFloat("Precio_Producto", item.PrecioProducto)
                    putInt("Cantidad_Producto", item.CantidadProducto)
                    putString("Fecha_Entrega", item.FechaEntrega)
                    putString("Horario_Entrega", item.HorarioEntrega)
                    putString("Nombre_Cliente", item.NombreCliente)
                    putString("Nombre_Calle", item.NombreCalle)
                    putString("Lugar_Entrega", item.LugarEntrega)
                    putString("Colonia", item.Colonia)
                    putString("Coordenadas", item.Coordenadas)
                    putString("Sin_Mensaje", item.SinMensaje)
                    putString("Dedicatoria", item.Dedicatoria)
                    putString("Envio_Sin_Nombre", item.EnvioSinNombre)
                    putString("Nombre_Emisor", item.NombreEmisor)
                    putParcelableArrayList("productos", ArrayList(Productos))
                }

                val navController = findNavController(holder.itemView)
                navController.navigate(R.id.navigation_Details_Order_Employed, bundle)
            }
        }
    }

    private suspend fun obtenerProductosParaPedido(uuid: String): List<Data_Productos> {
        return withContext(Dispatchers.IO) {
            val productos = mutableListOf<Data_Productos>()
            val objConexion = ClaseConexion().CadenaConexion()
            objConexion?.use { conn ->
                val query = """
                SELECT 
                    p.UUID_Producto, 
                    p.Cantidad_Producto, 
                    i.Precio_Producto, 
                    i.Img_Producto AS imageUrl, 
                    i.Nombre_Producto AS nombre
                FROM 
                    TbProductosPedido p
                JOIN 
                    TbInventario i ON p.UUID_Producto = i.UUID_Producto
                WHERE 
                    p.UUID_Pedido = ?
            """.trimIndent()

                val statement = conn.prepareStatement(query)
                statement.setString(1, uuid)
                val resultSet = statement.executeQuery()
                try {
                    while (resultSet.next()) {
                        val producto = Data_Productos(
                            imageUrl = resultSet.getString("imageUrl"),
                            nombre = resultSet.getString("nombre"),
                            cantidad = resultSet.getInt("Cantidad_Producto"),
                            precio = resultSet.getFloat("Precio_Producto")
                        )
                        productos.add(producto)
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    resultSet.close()
                }
            }
            productos
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}