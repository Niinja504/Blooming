package RecyclerViewHelpers

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_ShippingCost_Admin (view: View): RecyclerView.ViewHolder(view) {
    val Nombre_Zona: TextView = view.findViewById(R.id.lbl_Zona_ShippingCost)
    val Costo_Envio: TextView = view.findViewById(R.id.lbl_CostoEnvio_ShippingCost)
    val IC_Editar: ImageView = view.findViewById(R.id.IC_Edit_ShippingCost)
    val IC_Delete: ImageView = view.findViewById(R.id.IC_Delete_ShippingCost)
}