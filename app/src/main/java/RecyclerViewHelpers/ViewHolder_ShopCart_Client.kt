package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_ShopCart_Client (view: View): RecyclerView.ViewHolder(view) {
    val IMG_Producto_Sh: ImageView = view.findViewById(R.id.IMG_ShopCart_CardClient)
    val Nombre_Producto_Sh: TextView = view.findViewById(R.id.lbl_NombreProducto_ShopCart_CardClient)
    val Precio_Producto_Sh: TextView = view.findViewById(R.id.lbl_Precio_ShopCart_CardClient)
    val buttonMinus: ImageView = view.findViewById(R.id.button_minus)
    val buttonPlus: ImageView = view.findViewById(R.id.button_plus)
    val textViewValue: TextView = view.findViewById(R.id.textview_value)
    val IC_Delete: ImageView = view.findViewById(R.id.Ic_Delete)
}