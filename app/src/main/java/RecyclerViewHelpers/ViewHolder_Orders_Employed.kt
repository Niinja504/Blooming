package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Orders_Employed (view: View): RecyclerView.ViewHolder(view) {
    val CostoPedido : TextView = view.findViewById(R.id.txt_PrecioPedido_OrderDelivered_Employed)
    val FechaEntrega : TextView = view.findViewById(R.id.txt_FechaEntrega_OrderDelivered_Employed)
    val HoraEntrega : TextView = view.findViewById(R.id.txt_HoraEntrega_OrderDelivered_Employed)
    val NombreCliente : TextView = view.findViewById(R.id.txt_NombreCliente_OrderDelivered_Employed)
    val Colonia : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Employed)
    val Calle : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Employed)
}