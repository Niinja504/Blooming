package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_ShopCart_Employed (view: View): RecyclerView.ViewHolder(view) {
    val IMG_Producto_Sh: ImageView = view.findViewById(R.id.IMG_ShopCart_CardEmployed)
    val Nombre_Producto_Sh: TextView = view.findViewById(R.id.lbl_NombreProducto_ShopCart_CardEmployed)
    val Precio_Producto_Sh: TextView = view.findViewById(R.id.lbl_Precio_ShopCart_CardEmployed)
}