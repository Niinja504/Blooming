package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Inventory_Employed (view: View): RecyclerView.ViewHolder(view) {
    val IMG_Producto_View: ImageView = view.findViewById(R.id.IMG_Inventory_CardEmployed)
    val Nombre_Producto: TextView = view.findViewById(R.id.lbl_NombreProducto_CardEmployed)
    val Precio_Producto: TextView = view.findViewById(R.id.lbl_PrecioProducto_CardEmployed)
    val CantidadDisponible: TextView = view.findViewById(R.id.lbl_CantidaProducto_Disponible_Employed)
}