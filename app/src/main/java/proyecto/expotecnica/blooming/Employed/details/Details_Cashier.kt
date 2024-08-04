package proyecto.expotecnica.blooming.Employed.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class Details_Cashier : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_cashier_employed, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsCashier_Employed)


        IC_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_cash_register_employed)
        }

        return root
    }
}