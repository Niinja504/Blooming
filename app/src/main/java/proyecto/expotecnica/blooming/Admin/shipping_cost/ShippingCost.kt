package proyecto.expotecnica.blooming.Admin.shipping_cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class ShippingCost : Fragment()  {
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

        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddShippingCost_Admin)
        val AgregarCosto = root.findViewById<Button>(R.id.btn_AgregarCostoEnvio_Offers)

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_inventory_admin)
        }

        AgregarCosto.setOnClickListener {
            findNavController().navigate(R.id.action_AddShippingCost_Admin)
        }

        return root
    }
}