package proyecto.expotecnica.blooming.Admin.shipping_cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.Admin.ImageViewModel_Admin
import proyecto.expotecnica.blooming.R

class ShippingCost : Fragment()  {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
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

        val AgregarCosto = root.findViewById<Button>(R.id.btn_AgregarCostoEnvio_Offers)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_ShippingCost)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        AgregarCosto.setOnClickListener {
            findNavController().navigate(R.id.action_AddShippingCost_Admin)
        }

        return root
    }
}