package RecyclerViewHelpers

import DataC.Data_Productos
import DataC.Data_Sales
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

class Adaptador_Sales_Employed (var Datos: List<Data_Sales>): RecyclerView.Adapter<ViewHolder_Sales_Employed>() {
    fun EliminarDatos(uuid: String, posicion: Int){
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().CadenaConexion()
            objConexion?.use {
                val deleteArticulos = it.prepareStatement("DELETE FROM TbVentaArticulos WHERE UUID_Venta = ?")
                deleteArticulos.setString(1, uuid)
                deleteArticulos.executeUpdate()

                val deletePedido = it.prepareStatement("DELETE FROM TbVentaEncaja WHERE UUID_Venta = ?")
                deletePedido.setString(1, uuid)
                deletePedido.executeUpdate()

                // Commit
                val commit = it.prepareStatement("COMMIT")
                commit.executeUpdate()
            }

            // Actualizar la lista en el hilo principal
            withContext(Dispatchers.Main) {
                Datos = ListaDatos.toList()
                notifyItemRemoved(posicion)
                notifyDataSetChanged()
            }

        }

        Datos = ListaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Sales_Employed {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_sales_employed, parent, false)
        return  ViewHolder_Sales_Employed(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Sales_Employed, posicion: Int){
        val item = Datos[posicion]
        holder.CostoVenta.text = item.TotalVenta.toString()
        holder.FechaVenta.text = item.FechaVenta
        holder.HoraVenta.text = item.HoraVenta
        holder.NombreCliente.text = item.NombreCliente
        holder.NombreEmpleado.text = item.NombreEmpleado

        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val Productos = obtenerProductosDeVenta(item.uuid)
                val bundle = Bundle().apply {
                    putString("uuid", item.uuid)
                    putString("HoraVenta", item.HoraVenta)
                    putString("FechaVenta", item.FechaVenta)
                    putString("NombreCliente", item.NombreCliente)
                    putString("NombreEmpleado", item.NombreEmpleado)
                    putFloat("TotalVenta", item.TotalVenta)
                    putString("UUID_Producto", item.UUID_Producto)
                    putFloat("PrecioProducto", item.PrecioProducto)
                    putInt("CantidadProducto", item.CantidadProducto)
                    putParcelableArrayList("productos", ArrayList(Productos))
                }

                val navController = findNavController(holder.itemView)
                navController.navigate(R.id.navigation_Details_Sale_Employed, bundle)
            }
        }
    }

    private suspend fun obtenerProductosDeVenta(uuid: String): List<Data_Productos> {
        return withContext(Dispatchers.IO) {
            val productos = mutableListOf<Data_Productos>()
            val objConexion = ClaseConexion().CadenaConexion()
            objConexion?.use { conn ->
                val query = """
                SELECT 
                    a.UUID_Producto, 
                    a.Cantidad_Producto, 
                    a.Precio_Producto, 
                    i.Img_Producto AS imageUrl, 
                    i.Nombre_Producto AS nombre
                FROM 
                    TbVentaArticulos a
                JOIN 
                    TbInventario i ON a.UUID_Producto = i.UUID_Producto
                WHERE 
                    a.UUID_Venta = ?
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