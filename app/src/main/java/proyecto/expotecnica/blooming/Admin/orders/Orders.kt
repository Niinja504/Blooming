package proyecto.expotecnica.blooming.Admin.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import proyecto.expotecnica.blooming.Admin.ImageViewModel_Admin
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.R

class Orders : Fragment() {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders_admin, container, false)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Orders)

        val toggleGroup = root.findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)
        val btnPedidosPendientes = root.findViewById<MaterialButton>(R.id.btn_Pedidos_Pendientes_Admin)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        toggleGroup.check(btnPedidosPendientes.id)

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_Pedidos_Pendientes_Admin -> {
                        loadFragment(PendingOrdersFragment())
                    }
                    R.id.btn_Pedidos_Entregados_Admin -> {
                        loadFragment(DeliveredOrdersFragment())
                    }
                }
            }
        }

        // Cargar el Fragmento por defecto
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
