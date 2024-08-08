package RecyclerViewHelpers

import DataC.DataInventory
import DataC.ProductData_Employed
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.Employed.SharedViewModel_Product
import proyecto.expotecnica.blooming.R

class Adaptador_CashRegister_Employed(
    var Datos: List<DataInventory>, private val sharedViewModel: SharedViewModel_Product
) : RecyclerView.Adapter<ViewHolder_CashReg_Employed>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_CashReg_Employed {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_cash_register_employed, parent, false)
        return ViewHolder_CashReg_Employed(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_CashReg_Employed, position: Int) {
        val item = Datos[position]
        holder.Nombre_Producto.text = item.Nombre
        holder.Precio_Producto.text = item.Precio.toString()

        Glide.with(holder.IMG_Producto.context)
            .load(item.IMG_Product)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Producto)

        holder.Add_Producto.setOnClickListener {
            val productData = ProductData_Employed(
                uuid = item.uuid,
                img = item.IMG_Product,
                nombre = item.Nombre,
                precio = item.Precio,
                cantidad = 1,
                cantidadBodega = item.CantidadBode,
                categoriaFlores = item.CategoriaFlores,
                categoriaDiseno = item.CategoriaDiseno,
                categoriaEvento = item.CategoriaEventos,
                descripcion = item.Descripcion
            )
            sharedViewModel.addProduct(productData)
            Toast.makeText(it.context, "Se ha añadido al pedido", Toast.LENGTH_LONG).show()
        }

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
            navController.navigate(R.id.navigation_Details_ItemCashier, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}

