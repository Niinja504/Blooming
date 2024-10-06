package proyecto.expotecnica.blooming.Client.dashboard

import DataC.DataOffers_Admin
import RecyclerViewHelpers.Adaptador_Offers_Client
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.R


class Dashboard : Fragment() {
    private val imageViewModel: ImageViewModel_Client by activityViewModels()
    private lateinit var IMGUser: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_dashboard_client, container, false)

        IMGUser = root.findViewById(R.id.IMG_User_Dashboard)

        val RCV_Offers = root.findViewById<RecyclerView>(R.id.RCV_Offers_Client)
        //Asignarle un Layout al RecyclerView
        RCV_Offers.layoutManager = LinearLayoutManager(requireContext())

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(url)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
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
                val miAdaptador = Adaptador_Offers_Client(ProductosDB)
                RCV_Offers.adapter = miAdaptador
            }
        }

        return root
    }
}