package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Orders_Client (view: View): RecyclerView.ViewHolder(view)  {
    val CostoPedido : TextView = view.findViewById(R.id.txt_PrecioPedido_OrderDelivered_Client)
    val FechaEntrega : TextView = view.findViewById(R.id.txt_FechaEntrega_OrderDelivered_Client)
    val HoraEntrega : TextView = view.findViewById(R.id.txt_HoraEntrega_OrderDelivered_Client)
    val NombreCliente : TextView = view.findViewById(R.id.txt_NombreCliente_OrderDelivered_Client)
    val Colonia : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Client)
    val Calle : TextView = view.findViewById(R.id.txt_ColoniaEntrega_OrderDelivered_Client)
    val IC_delete : ImageView = view.findViewById(R.id.Ic_Delete_OrdesDelivered_Employed)
}