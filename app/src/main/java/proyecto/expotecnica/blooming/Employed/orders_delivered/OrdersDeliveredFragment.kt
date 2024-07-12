package proyecto.expotecnica.blooming.Employed.orders_delivered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import proyecto.expotecnica.blooming.databinding.FragmentOrderDeliveredEmployedBinding

class OrdersDeliveredFragment : Fragment()  {
    private var _binding: FragmentOrderDeliveredEmployedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(OrdersDeliveredViewModel::class.java)

        _binding = FragmentOrderDeliveredEmployedBinding.inflate(inflater, container, false)
        val root: View = binding.root


        notificationsViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}