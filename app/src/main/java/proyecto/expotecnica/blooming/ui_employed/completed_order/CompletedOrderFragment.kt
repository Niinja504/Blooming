package proyecto.expotecnica.blooming.ui_employed.completed_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import proyecto.expotecnica.blooming.databinding.FragmentCompletedOrderEmployedBinding


class CompletedOrderFragment : Fragment()  {
    private var _binding: FragmentCompletedOrderEmployedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(CompletedOrderViewModel::class.java)

        _binding = FragmentCompletedOrderEmployedBinding.inflate(inflater, container, false)
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