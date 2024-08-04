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

class Details_Cashier : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_cashier_employed, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsCashier_Employed)

        val IMG_Item = root.findViewById<ImageView>(R.id.ArchivoIMG_CashierDetails)

        val ImgRecibida = arguments?.getString("img")
        val NombreRecibido = arguments?.getString("nombre")
        val PrecioRecibido = arguments?.getFloat("precio")
        val CantidadDisponibleRecibida = arguments?.getInt("cantidadBodega")
        val CategoriaFloresRecibida = arguments?.getString("categoriaFlores")
        val CategoriaDisenoRecibida = arguments?.getString("categoriaDiseno")
        val CategoriaEventoRecibida = arguments?.getString("categoriaEvento")
        val DescripcionRecibida = arguments?.getString("descripcion")

        val lbl_Nombre = root.findViewById<TextView>(R.id.lbl_NombreArt_DetailsCashier_Employed)
        val lbl_Precio = root.findViewById<TextView>(R.id.lbl_Precio_DetailsCashier_Employed)
        val lbl_CantidadDisponible = root.findViewById<TextView>(R.id.lbl_CantidadDis_DetailsCashier_Employed)
        val lbl_CategoriaFlores = root.findViewById<TextView>(R.id.lbl_CategoriaFlores_DetailsCashier_Employed)
        val lbl_CategoriaDiseno = root.findViewById<TextView>(R.id.lbl_CategoriaDiseno_DetailsCashier_Employed)
        val lbl_CategoriaEvento = root.findViewById<TextView>(R.id.lbl_CategoriaEvento_DetailsCashier_Employed)
        val lbl_Descripcion = root.findViewById<TextView>(R.id.lbl_Descrip_DetailsCashier_Employed)

        lbl_Nombre.text = NombreRecibido
        lbl_Precio.text = PrecioRecibido.toString()
        lbl_CantidadDisponible.text = CantidadDisponibleRecibida.toString()
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
            findNavController().navigate(R.id.navigation_cash_register_employed)
        }

        return root
    }
}