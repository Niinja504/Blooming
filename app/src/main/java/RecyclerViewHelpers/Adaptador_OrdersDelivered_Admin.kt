package RecyclerViewHelpers

import DataC.DataOrdersDelivered_Admin
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Adaptador_OrdersDelivered_Admin (var Datos: List<DataOrdersDelivered_Admin>): RecyclerView.Adapter<ViewHolder_OrdersDelivered_Admin>() {
    fun EliminarDatos(uuid: String, posicion: Int){
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val DeletePedido= ObjConexion?.prepareStatement("DELETE TbPedido_Cliente WHERE UUID_Pedido = ?")!!
            DeletePedido.setString(1, uuid)
            DeletePedido.executeUpdate()

            val COMMIT = ObjConexion.prepareStatement("commit")
            COMMIT.executeUpdate()
        }
        Datos = ListaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_OrdersDelivered_Admin {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_orders_delivered_admin, parent, false)
        return  ViewHolder_OrdersDelivered_Admin(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_OrdersDelivered_Admin, posicion: Int){
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