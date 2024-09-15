package RecyclerViewHelpers

import DataC.Data_Productos
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.R

class ViewHolder_ProductDetails (view: View) : RecyclerView.ViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.IMG_Details_Orders_admin)
    private val nombreTextView: TextView = view.findViewById(R.id.lbl_NombreProducto_Orders_Details_Admin)
    private val cantidadTextView: TextView = view.findViewById(R.id.lbl_Cantidad_Details_Orders_Admin)
    private val precioTextView: TextView = view.findViewById(R.id.lbl_Precio_Details_Orders_Admin)

    fun bind(producto: Data_Productos) {
        Glide.with(imageView.context)
            .load(producto.imageUrl)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(imageView)

        cantidadTextView.text = producto.cantidad.toString()
        precioTextView.text = producto.precio.toString()
    }
}