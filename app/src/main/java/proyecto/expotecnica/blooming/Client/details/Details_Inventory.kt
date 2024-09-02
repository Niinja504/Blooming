package proyecto.expotecnica.blooming.Client.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.Admin.details.Details_Products
import proyecto.expotecnica.blooming.R

class Details_Inventory : Fragment() {
    companion object{
        fun newInstance(
            nombre: String,
            precio: Float,
            cantidad_bodega: Int,
            categoria_flores: String,
            categoria_diseno: String,
            categoria_eventos: String,
            descripcion: String
        ): Details_Products {
            val fragment = Details_Products()
            val args = Bundle()
            args.putString("nombre", nombre)
            args.putFloat("precio", precio)
            args.putInt("cantidadBodega", cantidad_bodega)
            args.putString("categoriaFlores", categoria_flores)
            args.putString("categoriaDiseno", categoria_diseno)
            args.putString("categoriaEvento", categoria_eventos)
            args.putString("descripcion", descripcion)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_inventory_client, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsInventory_Client)

        val IMG_Item = root.findViewById<ImageView>(R.id.ArchivoIMG_DetailsInventory_Client)

        val ImgRecibida = arguments?.getString("img")
        val NombreRecibido = arguments?.getString("nombre")
        val PrecioRecibido = arguments?.getFloat("precio")
        val CategoriaFloresRecibida = arguments?.getString("categoriaFlores")
        val CategoriaDisenoRecibida = arguments?.getString("categoriaDiseno")
        val CategoriaEventoRecibida = arguments?.getString("categoriaEvento")
        val DescripcionRecibida = arguments?.getString("descripcion")

        val lbl_Nombre = root.findViewById<TextView>(R.id.lbl_NombreArt_DetailsInventory_Client)
        val lbl_Precio = root.findViewById<TextView>(R.id.lbl_Precio_DetailsInventory_Client)
        val lbl_CategoriaFlores = root.findViewById<TextView>(R.id.lbl_CategoriaFlores_DetailsInventory_Client)
        val lbl_CategoriaDiseno = root.findViewById<TextView>(R.id.lbl_CategoriaDiseno_DetailsInventory_Client)
        val lbl_CategoriaEvento = root.findViewById<TextView>(R.id.lbl_CategoriaEvento_DetailsInventory_Client)
        val lbl_Descripcion = root.findViewById<TextView>(R.id.lbl_Descrip_DetailsInventory_Client)

        lbl_Nombre.text = NombreRecibido
        lbl_Precio.text = PrecioRecibido.toString()
        lbl_CategoriaFlores.text = CategoriaFloresRecibida
        lbl_CategoriaDiseno.text = CategoriaDisenoRecibida
        lbl_CategoriaEvento.text = CategoriaEventoRecibida
        lbl_Descripcion.text = DescripcionRecibida

        Glide.with(IMG_Item)
            .load(ImgRecibida)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(IMG_Item)

        IC_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop_client)
        }

        return root
    }
}