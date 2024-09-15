package RecyclerViewHelpers

import DataC.DataInventory
import DataC.ProductData_Client
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.Client.SharedViewModel_Product_Client
import proyecto.expotecnica.blooming.R
import java.sql.SQLException

class Adaptador_Shop_Client(
    var Datos: List<DataInventory>, private val sharedViewModel: SharedViewModel_Product_Client, private val ShareViewModel: ImageViewModel_Client
) : RecyclerView.Adapter<ViewHolder_Shop_Client>() {

    private var datosFiltrados = Datos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Shop_Client {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_shop_client, parent, false)
        return ViewHolder_Shop_Client(vista)
    }

    override fun getItemCount() = datosFiltrados.size

    override fun onBindViewHolder(holder: ViewHolder_Shop_Client, position: Int) {
        val item = datosFiltrados[position]
        holder.Nombre_Producto.text = item.Nombre
        holder.Precio_Producto.text = item.Precio.toString()

        Glide.with(holder.IMG_Producto.context)
            .load(item.IMG_Product)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Producto)

        var Actualiza_Estado = true

        CoroutineScope(Dispatchers.IO).launch {
            val favoritos = obtenerFavoritos()
            withContext(Dispatchers.Main) {
                holder.CB_Favorite.isChecked = favoritos.contains(item.uuid)
                Actualiza_Estado = false
            }
        }

        holder.Add_Producto.setOnClickListener {
            val productData = ProductData_Client(
                uuid = item.uuid,
                img = item.IMG_Product,
                nombre = item.Nombre,
                precio = item.Precio,
                cantidad = 1,
                categoriaFlores = item.CategoriaFlores,
                categoriaDiseno = item.CategoriaDiseno,
                categoriaEvento = item.CategoriaEventos,
                descripcion = item.Descripcion
            )
            sharedViewModel.addProduct(productData)
            Toast.makeText(it.context, "Se ha añadido al pedido", Toast.LENGTH_LONG).show()
        }

        holder.CB_Favorite.setOnCheckedChangeListener { _, isChecked ->
            if (!Actualiza_Estado) {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().CadenaConexion()
                    try {
                        objConexion?.use { conexion ->
                            if (isChecked) {
                                val addFavorito = conexion.prepareStatement("INSERT INTO TbArticulos_Favoritos (UUID_Cliente, UUID_Articulo) VALUES (?, ?)")
                                addFavorito.setString(1, ShareViewModel.uuid.value)
                                addFavorito.setString(2, item.uuid)
                                addFavorito.executeUpdate()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(holder.itemView.context, "Se añadió a favoritos", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val deleteFavorito = conexion.prepareStatement("DELETE FROM TbArticulos_Favoritos WHERE UUID_Cliente = ? AND UUID_Articulo = ?")
                                deleteFavorito.setString(1, ShareViewModel.uuid.value)
                                deleteFavorito.setString(2, item.uuid)
                                deleteFavorito.executeUpdate()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(holder.itemView.context, "Se eliminó de favoritos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: SQLException) {
                        if (e.message?.contains("ORA-00001") == true) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(holder.itemView.context, "El artículo ya está en favoritos", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(holder.itemView.context, "Error al actualizar favoritos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } finally {
                        objConexion?.close()
                    }
                }
            }
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
            navController.navigate(R.id.navigation_Details_Inventory_Client, bundle)
        }
    }

    fun filtrar(texto: String) {
        datosFiltrados = if (texto.isEmpty()) {
            Datos
        } else {
            Datos.filter {
                it.Nombre.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private suspend fun obtenerFavoritos(): Set<String> {
        val favoritos = mutableSetOf<String>()
        val objConexion = ClaseConexion().CadenaConexion()
        objConexion?.use { conexion ->
            val query = "SELECT UUID_Articulo FROM TbArticulos_Favoritos WHERE UUID_Cliente = ?"
            val statement = conexion.prepareStatement(query)
            statement.setString(1, ShareViewModel.uuid.value)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                favoritos.add(resultSet.getString("UUID_Articulo"))
            }
        }
        return favoritos
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}
