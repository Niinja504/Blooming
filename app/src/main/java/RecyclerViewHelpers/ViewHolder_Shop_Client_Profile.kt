package RecyclerViewHelpers

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Shop_Client_Profile (view: View): RecyclerView.ViewHolder(view) {
    val IMG_Producto: ImageView = view.findViewById(R.id.IMG_Shop_CardClient)
    val Nombre_Producto: TextView = view.findViewById(R.id.lbl_Nombre_Shop_CardClient)
    val Precio_Producto: TextView = view.findViewById(R.id.lbl_Precio_Shop_CardClient)
    val Add_Producto: ImageView = view.findViewById(R.id.Ic_AddProduct_Client)
    val CB_Favorite: CheckBox = view.findViewById(R.id.CB_Favorite)
}