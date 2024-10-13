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

        val IMG_Item = root.findViewById<ImageView>(R.id.ArchivoIMG_ShopCartDetails)

        arguments?.let { bundle ->
            val imgRecibida = bundle.getString("img")
            val nombreRecibido = bundle.getString("nombre")
            val precioRecibido = bundle.getFloat("precio")
            val cantidadBodegaRecibida = bundle.getInt("cantidadBodega")
            val categoriaFloresRecibida = bundle.getString("categoriaFlores")
            val categoriaDisenoRecibida = bundle.getString("categoriaDiseno")
            val categoriaEventoRecibida = bundle.getString("categoriaEvento")
            val descripcionRecibida = bundle.getString("descripcion")

            val lbl_Nombre = root.findViewById<TextView>(R.id.lbl_NombreArt_DetailsInventory_Employed)
            val lbl_Precio = root.findViewById<TextView>(R.id.lbl_Precio_DetailsInventory_Employed)
            val lbl_CantidadDisponible = root.findViewById<TextView>(R.id.lbl_CategoriaFlores_DetailsInventory_Employed)
            val lbl_CategoriaFlores = root.findViewById<TextView>(R.id.lbl_CategoriaDiseno_DetailsInventory_Employed)
            val lbl_CategoriaDiseno = root.findViewById<TextView>(R.id.lbl_CategoriaEvento_DetailsInventory_Employed)
            val lbl_CategoriaEvento = root.findViewById<TextView>(R.id.lbl_CantidadDis_DetailsCashier_Employed)
            val lbl_Descripcion = root.findViewById<TextView>(R.id.lbl_Descrip_DetailsInventory_Employed)

            lbl_Nombre.text = nombreRecibido
            lbl_Precio.text = precioRecibido.toString()
            lbl_CantidadDisponible.text = cantidadBodegaRecibida.toString()
            lbl_CategoriaFlores.text = categoriaFloresRecibida
            lbl_CategoriaDiseno.text = categoriaDisenoRecibida
            lbl_CategoriaEvento.text = categoriaEventoRecibida
            lbl_Descripcion.text = descripcionRecibida

            Glide.with(IMG_Item)
                .load(imgRecibida)
                .placeholder(R.drawable.profile_user)
                .error(R.drawable.profile_user)
                .into(IMG_Item)
        }

        IC_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop_cart_employed)
        }

        return root
    }
}