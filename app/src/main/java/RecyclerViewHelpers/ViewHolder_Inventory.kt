package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Inventory (view: View): RecyclerView.ViewHolder(view)  {
    val IMG_Producto_View: ImageView = view.findViewById(R.id.IMG_Inventory_CardAdmin)
    val Nombre_Producto: TextView = view.findViewById(R.id.lbl_NombreProducto_CardAdmin)
    val Precio_Producto: TextView = view.findViewById(R.id.lbl_PrecioProducto_CardAdmin)
    val IC_Editar: ImageView = view.findViewById(R.id.IC_Edit_Product_CardAdmin)
    val IC_Eliminar: ImageView = view.findViewById(R.id.IC_Delete_Product_CardAdmin)
}