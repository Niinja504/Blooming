package proyecto.expotecnica.blooming.Admin.shipping_cost

import DataC.DataShippingCost_Admin
import RecyclerViewHelpers.Adaptador_ShippingCost_Admin
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ShippingCost : Fragment()  {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
    private var miAdaptador: Adaptador_ShippingCost_Admin? = null
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
        val root = inflater.inflate(R.layout.fragment_shipping_cost_admin, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.Regresar_AddShippingCost_Admin)
        val AgregarCosto = root.findViewById<Button>(R.id.btn_AgregarCostoEnvio_Offers)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_ShippingCost)
        Buscador = root.findViewById(R.id.txt_Buscar_Shipping_Cost_Admin)
        val LimpiarBuscador = root.findViewById<ImageView>(R.id.IC_Limpiar_Bucador_Shipping_Cost_Admin)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        val RCV_Costo = root.findViewById<RecyclerView>(R.id.RCV_ShippingCost_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Costo.layoutManager = LinearLayoutManager(requireContext())

        IC_Regresar.setOnClickListener { 
            findNavController().navigate(R.id.navigation_inventory_admin)
        }

        LimpiarBuscador.setOnClickListener {
            Limpiar()
        }

        AgregarCosto.setOnClickListener {
            findNavController().navigate(R.id.action_AddShippingCost_Admin)
        }

        suspend fun MostrarDatos(): List<DataShippingCost_Admin> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM TbCostosEnvio")!!

            //Voy a guardar all lo que me traiga el Select

            val CostoEnvio = mutableListOf<DataShippingCost_Admin>()

            while (resultSet.next()){
                val Zona = resultSet.getString("Nombre_Zona")
                val Costo = resultSet.getFloat("Costo")
                val Coordenadas = resultSet.getString("Coordernadas_Google")
                val uuid = resultSet.getString("UUID_CostoEnvio")
                val Envio = DataShippingCost_Admin(uuid, Zona, Costo, Coordenadas)
                CostoEnvio.add(Envio)
            }
            return CostoEnvio
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val CostoDB = MostrarDatos()
            withContext(Dispatchers.Main){
                miAdaptador = Adaptador_ShippingCost_Admin(requireContext(), CostoDB)
                RCV_Costo.adapter = miAdaptador
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
}