package proyecto.expotecnica.blooming.Employed.shop_cart

import RecyclerViewHelpers.Adaptador_ShopCart_Employed
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.Employed.SharedViewModel_Product
import proyecto.expotecnica.blooming.R

class ShopCart : Fragment() {
    private val sharedViewModel: SharedViewModel_Product by activityViewModels()
    private lateinit var adapter: Adaptador_ShopCart_Employed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_shop_cart_employed, container, false)
        val RCV_Inventory = root.findViewById<RecyclerView>(R.id.RCV_ShopCart_Employed)
        RCV_Inventory.layoutManager = LinearLayoutManager(requireContext())

        adapter = Adaptador_ShopCart_Employed(emptyList(), sharedViewModel)
        RCV_Inventory.adapter = adapter

        sharedViewModel.productList.observe(viewLifecycleOwner) { productList ->
            adapter.Datos = productList
            adapter.notifyDataSetChanged()
        }

        return root
    }
}
