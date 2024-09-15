package RecyclerViewHelpers

import DataC.Data_Productos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.R

class Adaptador_Product_Details(private val productos: List<Data_Productos>) : RecyclerView.Adapter<ViewHolder_ProductDetails>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_ProductDetails {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_orders_details_admin, parent, false)
        return ViewHolder_ProductDetails(view)
    }

    override fun onBindViewHolder(holder: ViewHolder_ProductDetails, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount() = productos.size
}
