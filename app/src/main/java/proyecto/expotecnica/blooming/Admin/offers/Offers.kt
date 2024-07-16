package proyecto.expotecnica.blooming.Admin.offers

import DataClass.DataClassOffers
import Recycler_View.Adaptador_Offers
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Offers : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_offers, container, false)

        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsOffers)
        Regresar_Detalles.setOnClickListener {
            findNavController().navigate(R.id.navigation_offers_admin)
        }

        val UUIDRecibido = arguments?.getString("UUID_Oferta")
        val TituloRecibido = arguments?.getString("Titulo")

        val lblTitulo = root.findViewById<TextView>(R.id.lbl_DetalleOffers)
        lblTitulo.text = TituloRecibido

        // Carga la imagen en el ImageView
        /*
        lifecycleScope.launch {
            val imageUrl = obtenerImagenURL(UUIDRecibido ?: "")
            if (imageUrl != null) {
                Glide.with(this@DetailsOffers)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image) // Reemplaza esto con tu imagen de marcador de posición
                    .error(R.drawable.error_image) // Reemplaza esto con tu imagen de error
                    .into(root.findViewById<ImageView>(R.id.Im)
            }
        }*/

        return root
    }


    private suspend fun MostrarDatos(): List<DataClassOffers> {
        val ObjConexion = ClaseConexion().CadenaConexion()

        val statement = ObjConexion?.createStatement()
        val resultSet = statement?.executeQuery("SELECT * FROM TbOfertas")!!

        val Ofertas = mutableListOf<DataClassOffers>()

        while (resultSet.next()){
            val Titulo = resultSet.getString("Titulo")
            val IMGOffers = resultSet.getString("Img_oferta")
            val UUID = resultSet.getString("UUID_Oferta")
            val Oferta = DataClassOffers(UUID, Titulo, IMGOffers)
            Ofertas.add(Oferta)
        }
        return Ofertas
    }
}

