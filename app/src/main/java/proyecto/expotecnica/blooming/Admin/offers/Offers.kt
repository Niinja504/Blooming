package proyecto.expotecnica.blooming.Admin.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        //Variables que se van a utilizar
        val btn_AgregarOferta = root.findViewById<Button>(R.id.btn_AgregarOferta_Offers)


        btn_AgregarOferta.setOnClickListener{
            findNavController().navigate(R.id.AddOffers_admin)
        }

        return root
    }
}