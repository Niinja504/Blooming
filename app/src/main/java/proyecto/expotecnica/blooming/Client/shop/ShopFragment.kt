package proyecto.expotecnica.blooming.Client.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.databinding.FragmentShopClientBinding

class ShopFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_shop_client, container, false)

        //Variables que se van a utilizar
        val Ic_setting = root.findViewById<ImageView>(R.id.ic_Setting_Shop_Client)



        Ic_setting.setOnClickListener{
            findNavController().navigate(R.id.action_setting_client)
        }

        return root
    }
}