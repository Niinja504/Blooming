package proyecto.expotecnica.blooming.Admin.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class DetailsOffers : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_offers, container, false)


        //Variables que se van a utilizar
        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsOffers)


        Regresar_Detalles.setOnClickListener{
            findNavController().navigate(R.id.navigation_offers_admin)
        }

        val UUIDRecibido = arguments?.getString("UUID_Oferta")
        val TituloRecibido = arguments?.getString("Titulo")

        val lblTitulo = root.findViewById<TextView>(R.id.lbl_DetalleOffers)
        lblTitulo.text = TituloRecibido

        return root
    }
}