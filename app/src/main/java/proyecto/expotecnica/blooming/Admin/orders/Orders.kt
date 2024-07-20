package proyecto.expotecnica.blooming.Admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import proyecto.expotecnica.blooming.R

class Orders : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_orders_admin, container, false)

        val toggleGroup = root.findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_Pedidos_Pendientes_Admin -> {
                        // Load the Pending Orders Fragment
                        loadFragment(PendingOrdersFragment())
                    }
                    R.id.btn_Pedidos_Entregados_Admin -> {
                        // Load the Delivered Orders Fragment
                        loadFragment(DeliveredOrdersFragment())
                    }
                }
            }
        }

        //Fragment por defecto
        loadFragment(PendingOrdersFragment())

        return root
    }

    class PendingOrdersFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_back_orders_admin, container, false)
        }
    }

    class DeliveredOrdersFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_orders_delivered_admin, container, false)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow() // Usar commitNow en lugar de commit
    }
}
