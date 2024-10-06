package RecyclerViewHelpers

import DataC.Data_Orders
import DataC.Data_Productos
import android.app.AlertDialog
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

class Adaptador_Orders_Admin (var Datos: List<Data_Orders>): RecyclerView.Adapter<ViewHolder_Orders_Admin>() {
    fun EliminarDatos(uuid: String, posicion: Int){
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().CadenaConexion()

            objConexion?.let { conexion ->
                try {
                    val deleteProductos = objConexion?.prepareStatement("DELETE FROM TbProductosPedido WHERE UUID_Pedido = ?")
                    deleteProductos?.setString(1, uuid)
                    deleteProductos?.executeUpdate()

                    val deleteHorario = objConexion?.prepareStatement("DELETE FROM TbHorarioPedido WHERE UUID_Pedido = ?")
                    deleteHorario?.setString(1, uuid)
                    deleteHorario?.executeUpdate()

                    val deleteDireccion = objConexion?.prepareStatement("DELETE FROM TbDireccionPedido WHERE UUID_Pedido = ?")
                    deleteDireccion?.setString(1, uuid)
                    deleteDireccion?.executeUpdate()

                    val deleteDedicatoria = objConexion?.prepareStatement("DELETE FROM TbDedicatorias WHERE UUID_Pedido = ?")
                    deleteDedicatoria?.setString(1, uuid)
                    deleteDedicatoria?.executeUpdate()

                    val deletePedido = objConexion?.prepareStatement("DELETE FROM TbPedido_Cliente WHERE UUID_Pedido = ?")
                    deletePedido?.setString(1, uuid)
                    deletePedido?.executeUpdate()

                    val commit = objConexion?.prepareStatement("COMMIT")
                    commit?.executeUpdate()
                }   catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    conexion.close()
                }
            }
        }

        Datos = ListaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Orders_Admin {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_orders_admin, parent, false)
        return  ViewHolder_Orders_Admin(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Orders_Admin, posicion: Int){
        val item = Datos[posicion]
        holder.CostoPedido.text = item.SubTotal.toString()
        holder.FechaEntrega.text = item.FechaEntrega
        holder.HoraEntrega.text = item.HorarioEntrega
        holder.NombreCliente.text = item.NombreCliente
        holder.Colonia.text = item.Colonia
        holder.Calle.text = item.NombreCalle

        holder.IC_delete.setOnClickListener{
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto
            val context = holder.itemView.context

            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                EliminarDatos(item.uuid, posicion)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

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
                navController.navigate(R.id.navigation_Details_Orders_admin, bundle)
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