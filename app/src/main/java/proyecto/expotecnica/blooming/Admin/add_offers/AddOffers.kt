package proyecto.expotecnica.blooming.Admin.add_offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class AddOffers : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_offers, container, false)

        //Variables que se van a utilizar
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddOffers_Offers)


        Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_offers_admin)
        }

        return root
    }
}