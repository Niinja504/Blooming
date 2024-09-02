package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_CashReg_Employed (view: View): RecyclerView.ViewHolder(view) {
    val IMG_Producto: ImageView = view.findViewById(R.id.IMG_CashReg_CardEmployed)
    val Nombre_Producto: TextView = view.findViewById(R.id.lbl_Nombre_CashReg_CardEmployed)
    val Precio_Producto: TextView = view.findViewById(R.id.lbl_Precio_CashReg_CardEmployed)
    val Add_Producto: ImageView = view.findViewById(R.id.Ic_AddProduct)
}