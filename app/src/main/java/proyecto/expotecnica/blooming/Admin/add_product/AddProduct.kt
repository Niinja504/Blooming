package proyecto.expotecnica.blooming.Admin.add_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import proyecto.expotecnica.blooming.R

class AddProduct : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_product_inventory, container, false)

        //Variables que se van a utilizar
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddProduct_Inventory)


        Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_inventory_admin)
        }


        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        val images = listOf(
            R.drawable.ic_key,
            R.drawable.ic_home,
            R.drawable.ic_cash_register
        )

        viewPager.adapter = ImgCarrusel(images)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Customize tab if needed
        }.attach()
    }
}