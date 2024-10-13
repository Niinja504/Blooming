package proyecto.expotecnica.blooming.Admin.offers

import DataC.DataOffers_Admin
import RecyclerViewHelpers.Adaptador_Offers_Admin
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Admin.ImageViewModel_Admin
import proyecto.expotecnica.blooming.R

class Offers : Fragment() {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
    private var miAdaptador: Adaptador_Offers_Admin? = null
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
        val root = inflater.inflate(R.layout.fragment_offers_admin, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.Regresar_Offers_Admin)
        val AgregarOferta = root.findViewById<Button>(R.id.btn_AgregarOferta_Offers)
        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Offers)
        Buscador = root.findViewById(R.id.txt_Buscar_Offers_Admin)
        val LimpiarBuscador = root.findViewById<ImageView>(R.id.IC_Limpiar_Bucador_Offers_Admin)

        val RCV_Offers = root.findViewById<RecyclerView>(R.id.RCV_Offers_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Offers.layoutManager = LinearLayoutManager(requireContext())

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        IC_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_inventory_admin)
        }

        LimpiarBuscador.setOnClickListener {
            Limpiar()
            Teclado()
        }

        AgregarOferta.setOnClickListener{
            findNavController().navigate(R.id.action_AddOffers_Admin)
        }


        suspend fun MostrarDatos(): List<DataOffers_Admin> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val ResultSet = statement?.executeQuery("SELECT * FROM TbOfertas")!!

            //Voy a guardar all lo que me traiga el Select

            val Ofertas = mutableListOf<DataOffers_Admin>()

            while (ResultSet.next()){
                val UUID_Oferta = ResultSet.getString("UUID_Oferta")
                val UUIDProducts = ResultSet.getString("UUID_Producto")
                val Titulo = ResultSet.getString("Titulo")
                val Porcentaje = ResultSet.getString("Porcentaje_Oferta")
                val Descripcion = ResultSet.getString("Decripcion_Oferta")
                val IMG_Offer = ResultSet.getString("Img_oferta")
                val Oferta = DataOffers_Admin(UUID_Oferta, UUIDProducts, Titulo, Porcentaje, Descripcion, IMG_Offer)
                Ofertas.add(Oferta)
            }
            return Ofertas
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val ProductosDB = MostrarDatos()
            withContext(Dispatchers.Main){
                miAdaptador = Adaptador_Offers_Admin(requireContext(), ProductosDB)
                RCV_Offers.adapter = miAdaptador
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

