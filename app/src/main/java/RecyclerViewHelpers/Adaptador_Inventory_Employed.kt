package RecyclerViewHelpers

import DataC.DataInventory
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
import proyecto.expotecnica.blooming.R

class Adaptador_Inventory_Employed (var Datos: List<DataInventory>) : RecyclerView.Adapter<ViewHolder_Inventory_Employed>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Inventory_Employed {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_inventory_employed, parent, false)
        return ViewHolder_Inventory_Employed(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Inventory_Employed, position: Int) {
        val item = Datos[position]
        holder.Nombre_Producto.text = item.Nombre
        holder.Precio_Producto.text = item.Precio.toString()
        holder.CantidadDisponible.text = item.CantidadBode.toString()

        Glide.with(holder.IMG_Producto_View.context)
            .load(item.IMG_Product)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Producto_View)


        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("img", item.IMG_Product)
                putString("nombre", item.Nombre)
                putFloat("precio", item.Precio)
                putInt("cantidadBodega", item.CantidadBode)
                putString("categoriaFlores", item.CategoriaFlores)
                putString("categoriaDiseno", item.CategoriaDiseno)
                putString("categoriaEvento", item.CategoriaEventos)
                putString("descripcion", item.Descripcion)
            }

            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_Inventory, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}
