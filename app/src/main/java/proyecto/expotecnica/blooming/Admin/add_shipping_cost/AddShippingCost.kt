package proyecto.expotecnica.blooming.Admin.add_shipping_cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import proyecto.expotecnica.blooming.R

class AddShippingCost : Fragment() {
    private lateinit var dialogView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_shipping_cost_admin, container, false)
        dialogView = root



        return root
    }
}