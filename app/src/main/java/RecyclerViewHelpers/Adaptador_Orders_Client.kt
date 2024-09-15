package RecyclerViewHelpers

import DataC.Data_Orders
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

class Adaptador_Orders_Client (var Datos: List<Data_Orders>): RecyclerView.Adapter<ViewHolder_Orders_Client>() {
    fun EliminarDatos(uuid: String, posicion: Int) {
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().CadenaConexion()

            objConexion?.let { conexion ->
                try {
                    val deleteProductos = conexion.prepareStatement("DELETE FROM TbProductosPedido WHERE UUID_Pedido = ?")
                    deleteProductos.setString(1, uuid)
                    deleteProductos.executeUpdate()

                    val deleteHorario = conexion.prepareStatement("DELETE FROM TbHorarioPedido WHERE UUID_Pedido = ?")
                    deleteHorario.setString(1, uuid)
                    deleteHorario.executeUpdate()

                    val deleteDireccion = conexion.prepareStatement("DELETE FROM TbDireccionPedido WHERE UUID_Pedido = ?")
                    deleteDireccion.setString(1, uuid)
                    deleteDireccion.executeUpdate()

                    val deleteDedicatorias = conexion.prepareStatement("DELETE FROM TbDedicatorias WHERE UUID_Pedido = ?")
                    deleteDedicatorias.setString(1, uuid)
                    deleteDedicatorias.executeUpdate()

                    val deletePedido = conexion.prepareStatement("DELETE FROM TbPedido_Cliente WHERE UUID_Pedido = ?")
                    deletePedido.setString(1, uuid)
                    deletePedido.executeUpdate()

                    val commit = conexion.prepareStatement("COMMIT")
                    commit.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    conexion.close()
                }
            }

            Datos = ListaDatos.toList()
            withContext(Dispatchers.Main) {
                notifyItemRemoved(posicion)
                notifyDataSetChanged()
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Orders_Client {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_orders_client, parent, false)
        return  ViewHolder_Orders_Client(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Orders_Client, posicion: Int){
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
            val bundle = Bundle().apply {
                putString("FechaVenta", item.FechaVenta)
                putString("HoraVenta", item.HoraVenta)
                putFloat("SubTotal", item.SubTotal)
                putString("UUID_Producto", item.UUID_Producto)
            }

            val navController = findNavController(holder.itemView)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }

}