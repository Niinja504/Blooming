package proyecto.expotecnica.blooming.ui_client.delivery_date

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.databinding.FragmentDeliveryDateClientBinding

class DeliveryDateFragment : Fragment() {
    private var _binding: FragmentDeliveryDateClientBinding? = null

    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(DeliveryDateViewModel::class.java)

        _binding = FragmentDeliveryDateClientBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Llamar a todos los elementos
        val RadioButton8AM: RadioButton = binding.RDHora8AMDateClient
        val RadioButton12AM: RadioButton = binding.RDHora12AMDateClient
        val RadioButton4PM: RadioButton = binding.RDHora4AMDateClient

        homeViewModel.text.observe(viewLifecycleOwner) {
            // Observador del ViewModel
        }

        // Funciones
        RadioButton8AM.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                RadioButton8AM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            } else {
                RadioButton8AM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            }
        }

        RadioButton12AM.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                RadioButton12AM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            } else {
                RadioButton12AM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            }
        }

        RadioButton4PM.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                RadioButton4PM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            } else {
                RadioButton4PM.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Brown))
            }
        }
        //Cierre


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
