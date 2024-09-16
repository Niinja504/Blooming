package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Sales_Employed (view: View): RecyclerView.ViewHolder(view) {
    val CostoVenta : TextView = view.findViewById(R.id.txt_PrecioVenta_Venta_Employed)
    val FechaVenta : TextView = view.findViewById(R.id.txt_FechaVenta_Venta_Employed)
    val HoraVenta : TextView = view.findViewById(R.id.txt_HoraVenta_Venta_Employed)
    val NombreCliente : TextView = view.findViewById(R.id.txt_NombreCliente_Venta_Employed)
    val NombreEmpleado : TextView = view.findViewById(R.id.txt_NombreEmpleado_Venta_Employed)
}