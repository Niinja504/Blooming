package proyecto.expotecnica.blooming.Admin.offers

import DataC.DataOffers
import RecyclerViewHelpers.Adaptador_Offers
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Offers : Fragment() {
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

        val AgregarOferta = root.findViewById<Button>(R.id.btn_AgregarOferta_Offers)

        val RCV_Offers = root.findViewById<RecyclerView>(R.id.RCV_Offers_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Offers.layoutManager = LinearLayoutManager(requireContext())

        AgregarOferta.setOnClickListener{
            findNavController().navigate(R.id.action_AddOffers_Admin)
        }

        suspend fun MostrarDatos(): List<DataOffers> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val ResultSet = statement?.executeQuery("SELECT * FROM TbOfertas")!!

            //Voy a guardar all lo que me traiga el Select

            val Ofertas = mutableListOf<DataOffers>()

            while (ResultSet.next()){
                val UUID_Oferta = ResultSet.getString("UUID_Oferta")
                val UUIDProducts = ResultSet.getString("UUID_Producto")
                val Titulo = ResultSet.getString("Titulo")
                val Porcentaje = ResultSet.getString("Porcentaje_Oferta")
                val Descripcion = ResultSet.getString("Decripcion_Oferta")
                val IMG_Offer = ResultSet.getString("Img_oferta")
                val Oferta = DataOffers(UUID_Oferta, UUIDProducts, Titulo, Porcentaje, Descripcion, IMG_Offer)
                Ofertas.add(Oferta)
            }
            return Ofertas
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val ProductosDB = MostrarDatos()
            withContext(Dispatchers.Main){
                val miAdaptador = Adaptador_Offers(ProductosDB)
                RCV_Offers.adapter = miAdaptador
            }
        }

        return root
    }
}

