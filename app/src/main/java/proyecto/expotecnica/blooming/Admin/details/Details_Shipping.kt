package proyecto.expotecnica.blooming.Admin.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class Details_Shipping : Fragment()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_shipping_cost_admin, container, false)

        val Ic_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_Details_ShippingCost)


        val ImgRecibida = arguments?.getString("img")
        val NombreRecibido = arguments?.getString("nombre")
        val PrecioRecibido = arguments?.getFloat("precio")


        val lbl_Zona = root.findViewById<TextView>(R.id.lbl_Zona_Details_ShippingCost_Admin)
        val lbl_Costo = root.findViewById<TextView>(R.id.lbl_Costo_Details_ShippingCost_Admin)

        lbl_Zona.text = NombreRecibido
        lbl_Costo.text = PrecioRecibido.toString()

        Ic_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_shipping_cost_admin)
        }

        return root
    }
}