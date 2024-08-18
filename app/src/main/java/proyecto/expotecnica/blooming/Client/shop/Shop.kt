package proyecto.expotecnica.blooming.Client.shop

import DataC.DataInventory
import RecyclerViewHelpers.Adaptador_Shop_Client
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.Client.SharedViewModel_Product_Client

class Shop : Fragment() {
    private val imageViewModel: ImageViewModel_Client by activityViewModels()
    private val sharedViewModel: SharedViewModel_Product_Client by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_shop_client, container, false)

        val RCV_Cash = root.findViewById<RecyclerView>(R.id.RCV_Shop_Client)
        //Asignarle un Layout al RecyclerView
        RCV_Cash.layoutManager = LinearLayoutManager(requireContext())

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        RCV_Cash.layoutManager = gridLayoutManager

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Shop)

        // Observar los cambios en imageUrl
        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        suspend fun MostrarDatos(): List<DataInventory> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val ResultSet = statement?.executeQuery("SELECT * FROM TbInventario")!!

            //Voy a guardar all lo que me traiga el Select

            val Productos = mutableListOf<DataInventory>()

            while (ResultSet.next()){
                val IMG_Produc = ResultSet.getString("Img_Producto")
                val Nombre = ResultSet.getString("Nombre_Producto")
                val Precio = ResultSet.getFloat("Precio_Producto")
                val CantidadBode = ResultSet.getInt("Cantidad_Bodega_Productos")
                val CategoriaFlores = ResultSet.getString("Categoria_Flores")
                val CategoriaDiseno = ResultSet.getString("Categoria_Diseno")
                val CategoriaEvento = ResultSet.getString("Categoria_Evento")
                val Descripcion = ResultSet.getString("Descripcion_Producto")
                val uuid = ResultSet.getString("UUID_Producto")
                val Producto = DataInventory(uuid, IMG_Produc, Nombre, Precio, CantidadBode, CategoriaFlores, CategoriaDiseno, CategoriaEvento, Descripcion)
                Productos.add(Producto)
            }
            return Productos
        }

        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = MostrarDatos()
            withContext(Dispatchers.Main) {
                val miAdaptador = Adaptador_Shop_Client(productosDB, sharedViewModel)
                RCV_Cash.adapter = miAdaptador
            }
        }

        return root
    }
}