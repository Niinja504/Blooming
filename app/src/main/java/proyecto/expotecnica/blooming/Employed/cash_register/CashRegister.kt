package proyecto.expotecnica.blooming.Employed.cash_register

import DataC.DataInventory
import RecyclerViewHelpers.Adaptador_CashRegister_Employed
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Employed.ImageViewModel_Employed
import proyecto.expotecnica.blooming.Employed.SharedViewModel_Product_Employed
import proyecto.expotecnica.blooming.R

class CashRegister : Fragment() {
    private val imageViewModel: ImageViewModel_Employed by activityViewModels()
    private val sharedViewModel: SharedViewModel_Product_Employed by activityViewModels()
    private var miAdaptador: Adaptador_CashRegister_Employed? = null
    private lateinit var Buscador: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_cash_register_employed, container, false)

        val IC_Notifications = root.findViewById<ImageView>(R.id.IC_Notifications_Employed)
        val RCV_Cash = root.findViewById<RecyclerView>(R.id.RCV_CashRegister_Employed)
        RCV_Cash.layoutManager = GridLayoutManager(requireContext(), 2)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_CashRegister)
        Buscador = root.findViewById(R.id.txt_Buscar_CashRegister)
        val LimpiarBuscador = root.findViewById<ImageView>(R.id.IC_Limpiar_Bucador_CashRegister)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        IC_Notifications.setOnClickListener {
            findNavController().navigate(R.id.navigation_notifications_employed)
        }

        LimpiarBuscador.setOnClickListener {
            Limpiar()
            Teclado()
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
                val IMG_Produc = ResultSet.getString("Img_Producto") ?: ""
                val Nombre = ResultSet.getString("Nombre_Producto") ?: "Sin Nombre"
                val Precio = ResultSet.getFloat("Precio_Producto")
                val CantidadBode = ResultSet.getInt("Cantidad_Bodega_Productos")
                val CategoriaFlores = ResultSet.getString("Categoria_Flores") ?: "Sin Categoría"
                val CategoriaDiseno = ResultSet.getString("Categoria_Diseno") ?: "Sin Categoría"
                val CategoriaEvento = ResultSet.getString("Categoria_Evento") ?: "Sin Categoría"
                val Descripcion = ResultSet.getString("Descripcion_Producto") ?: "Sin Descripción"
                val uuid = ResultSet.getString("UUID_Producto")
                val Producto = DataInventory(uuid, IMG_Produc, Nombre, Precio, CantidadBode, CategoriaFlores, CategoriaDiseno, CategoriaEvento, Descripcion)
                Productos.add(Producto)
            }
            return Productos
        }

        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = MostrarDatos()
            withContext(Dispatchers.Main) {
                miAdaptador = Adaptador_CashRegister_Employed(productosDB, sharedViewModel)
                RCV_Cash.adapter = miAdaptador
            }
        }

        //Buscador que funciona por medio del nombre =)
        Buscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                miAdaptador?.filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }

    fun Limpiar(){
        Buscador.text.clear()
        Buscador.clearFocus()
    }

    fun Teclado() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentView = activity?.currentFocus
        currentView?.clearFocus()
        (view as? View)?.let { v ->
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}
