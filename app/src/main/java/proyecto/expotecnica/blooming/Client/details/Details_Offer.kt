package proyecto.expotecnica.blooming.Client.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.Admin.details.Details_Offers
import proyecto.expotecnica.blooming.R

class Details_Offer : Fragment() {
    companion object{
        fun newInstance(
            titulo: String,
            porcentaje: String,
            descripcion: String
        ): Details_Offers {
            val fragment = Details_Offers()
            val args = Bundle()
            args.putString("nombre", titulo)
            args.putString("porcentaje", porcentaje)
            args.putString("descripcion", descripcion)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_offers_client, container, false)

        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsOffers_Client)

        val IMG_Offer = root.findViewById<ImageView>(R.id.ArchivoIMG_OfferDetails_Client)

        val ImgRecibida = arguments?.getString("img")
        val TituloRecibido = arguments?.getString("titulo")
        val PorcentajeRecibido = arguments?.getString("porcentaje")
        val DescripcionRecibida = arguments?.getString("descripcion")

        val lbl_Titulo = root.findViewById<TextView>(R.id.lbl_Titulo_DetailsOffer_Client)
        val lbl_Porcentaje = root.findViewById<TextView>(R.id.lbl_Porcentaje_DetailsOffer_Client)
        val lbl_Descripcion = root.findViewById<TextView>(R.id.lbl_Descripcion_DetailsOffer_Client)

        lbl_Titulo.text = TituloRecibido
        lbl_Porcentaje.text = PorcentajeRecibido
        lbl_Descripcion.text = DescripcionRecibida

        Glide.with(IMG_Offer)
            .load(ImgRecibida)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(IMG_Offer)

        Regresar_Detalles.setOnClickListener{
            findNavController().navigate(R.id.navigation_dashboard_client)
        }

        return root
    }
}