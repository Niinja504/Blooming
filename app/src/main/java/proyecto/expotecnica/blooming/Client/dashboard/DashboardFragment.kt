package proyecto.expotecnica.blooming.Client.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R


class DashboardFragment : Fragment() {
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

        //Variables que se van a utilizar
        val Ic_setting = root.findViewById<ImageView>(R.id.ic_Settings_client)
        val VerMas_ofertas = root.findViewById<TextView>(R.id.lbl_VerMasOfertas_client)



        Ic_setting.setOnClickListener{
            findNavController().navigate(R.id.action_setting_client)
        }

        VerMas_ofertas.setOnClickListener{
            findNavController().navigate(R.id.action_ver_mas_ofertas)
        }

        return root
    }
}