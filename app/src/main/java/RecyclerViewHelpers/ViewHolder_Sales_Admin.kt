package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Sales_Admin (view: View): RecyclerView.ViewHolder(view) {
    val CostoVenta : TextView = view.findViewById(R.id.txt_PrecioVenta_Venta_Admin)
    val FechaVenta : TextView = view.findViewById(R.id.txt_FechaVenta_Venta_Admin)
    val HoraVenta : TextView = view.findViewById(R.id.txt_HoraVenta_Venta_Admin)
    val NombreCliente : TextView = view.findViewById(R.id.txt_NombreCliente_Venta_Admin)
    val NombreEmpleado : TextView = view.findViewById(R.id.txt_NombreEmpleado_Venta_Admin)
    val IC_delete : ImageView = view.findViewById(R.id.Ic_Delete_Ventas_Admin)
}