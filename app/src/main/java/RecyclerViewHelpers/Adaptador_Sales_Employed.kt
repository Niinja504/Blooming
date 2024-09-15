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
                val Productos = obtenerProductosParaPedido(item.uuid)
                val bundle = Bundle().apply {
                    putString("uuid", item.uuid)
                    putString("HoraVenta", item.HoraVenta)
                    putParcelableArrayList("productos", ArrayList(Productos))
                }

                val navController = findNavController(holder.itemView)
            }
        }
    }

    private suspend fun obtenerProductosParaPedido(uuid: String): List<Data_Productos> {
        return withContext(Dispatchers.IO) {
            val productos = mutableListOf<Data_Productos>()
            val objConexion = ClaseConexion().CadenaConexion()
            objConexion?.use {
                val query = it.prepareStatement("SELECT * FROM TbVentaArticulos WHERE UUID_Venta = ?")
                query.setString(1, uuid)
                val resultSet = query.executeQuery()

                try {
                    while (resultSet.next()) {
                        val producto = Data_Productos(
                            imageUrl = resultSet.getString("UUID_Producto"),
                            cantidad = resultSet.getInt("Cantidad_Producto"),
                            precio = resultSet.getFloat("Precio_Producto")
                        )
                        productos.add(producto)
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
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