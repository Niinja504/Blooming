package proyecto.expotecnica.blooming.Admin.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class InventoryFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_inventory_admin, container, false)

        //Variables que se van a utilizar
        val AgregarProducto = root.findViewById<Button>(R.id.btn_AgregarProducto_Inventory)


        AgregarProducto.setOnClickListener{
            findNavController().navigate(R.id.action_AddProduct_admin)
        }

        return root
    }
}

