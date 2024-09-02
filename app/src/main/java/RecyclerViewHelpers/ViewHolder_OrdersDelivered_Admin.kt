package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_OrdersDelivered_Admin (view: View): RecyclerView.ViewHolder(view) {
    val CostoPedido : TextView = view.findViewById(R.id.txt_PrecioPedido_OrderDelivered_Admin)
    val FechaEntrega : TextView = view.findViewById(R.id.txt_FechaEntrega_OrderDelivered_Admin)
    val HoraEntrega : TextView = view.findViewById(R.id.txt_HoraEntrega_OrderDelivered_Admin)
    val NombreCliente : TextView = view.findViewById(R.id.txt_NombreCliente_OrderDelivered_Admin)
    val Colonia : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Admin)
    val Calle : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Admin)
    val IC_delete : ImageView = view.findViewById(R.id.Ic_Delete_OrdesDelivered_Admin)
}