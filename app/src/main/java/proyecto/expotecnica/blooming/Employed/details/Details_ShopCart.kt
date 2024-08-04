package proyecto.expotecnica.blooming.Employed.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.R

class Details_ShopCart : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_shop_cart_employed, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_Details_ShopCart_Employed)

        arguments?.let { bundle ->
            val img = bundle.getString("img")
            val nombre = bundle.getString("nombre")
            val precio = bundle.getFloat("precio")
            val cantidadBodega = bundle.getInt("cantidadBodega")
            val categoriaFlores = bundle.getString("categoriaFlores")
            val categoriaDiseno = bundle.getString("categoriaDiseno")
            val categoriaEvento = bundle.getString("categoriaEvento")
            val descripcion = bundle.getString("descripcion")
        }

        IC_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop_cart_employed)
        }

        return root
    }
}