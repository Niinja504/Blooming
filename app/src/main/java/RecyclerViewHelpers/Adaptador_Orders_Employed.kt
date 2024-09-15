package RecyclerViewHelpers

import DataC.Data_Orders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

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