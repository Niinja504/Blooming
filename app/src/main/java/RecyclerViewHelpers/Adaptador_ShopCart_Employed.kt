package RecyclerViewHelpers

import DataC.ProductData_Employed
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.Employed.SharedViewModel_Product
import proyecto.expotecnica.blooming.R

class Adaptador_ShopCart_Employed(
    var Datos: List<ProductData_Employed>, private val sharedViewModel: SharedViewModel_Product,
    private val ActualizaTotalVenta: () -> Unit
) : RecyclerView.Adapter<ViewHolder_ShopCart_Employed>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_ShopCart_Employed {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_shop_cart_employed, parent, false)
        return ViewHolder_ShopCart_Employed(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_ShopCart_Employed, position: Int) {
        val item = Datos[position]
        holder.Nombre_Producto_Sh.text = item.nombre
        holder.Precio_Producto_Sh.text = item.precio.toString()
        holder.textViewValue.text = item.cantidad.toString()

        Glide.with(holder.IMG_Producto_Sh.context)
            .load(item.img)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Producto_Sh)

        holder.buttonMinus.setOnClickListener {
            if (item.cantidad > 1) {
                item.cantidad--
                holder.textViewValue.text = item.cantidad.toString()
                ActualizaTotalVenta()
            }
        }

        holder.buttonPlus.setOnClickListener {
            item.cantidad++
            holder.textViewValue.text = item.cantidad.toString()
            ActualizaTotalVenta()
        }

        holder.IC_Delete.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                sharedViewModel.EliminarProducto(item.uuid)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("img", item.img)
                putString("nombre", item.nombre)
                putFloat("precio", item.precio)
                putInt("cantidadBodega", item.cantidadBodega)
                putString("categoriaFlores", item.categoriaFlores)
                putString("categoriaDiseno", item.categoriaDiseno)
                putString("categoriaEvento", item.categoriaEvento)
                putString("descripcion", item.descripcion)
            }

            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_ShopCart, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}
