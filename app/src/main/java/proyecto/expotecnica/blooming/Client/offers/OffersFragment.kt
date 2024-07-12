package proyecto.expotecnica.blooming.Client.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.databinding.FragmentOffersClientBinding

class OffersFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_offers_client, container, false)

        //Variables que se van a utilizar
        val Ic_Regresar = root.findViewById<ImageView>(R.id.ic_Regresar_Offers_Client)

        Ic_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard_client)
        }

        return root
    }
}