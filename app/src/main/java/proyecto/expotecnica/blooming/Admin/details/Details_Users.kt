package proyecto.expotecnica.blooming.Admin.details

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

class Details_Users : Fragment()  {
    companion object{
        fun newInstance(
            nombres: String,
            apellidos: String,
            nombre_de_usuario: String,
            num_telefono: String,
            correo: String
        ): Details_Users{
            val fragment = Details_Users()
            val args = Bundle()
            args.putString("nombres", nombres)
            args.putString("apellidos", apellidos)
            args.putString("nombre_de_usuario", nombre_de_usuario)
            args.putString("telefono", num_telefono)
            args.putString("correo", correo)
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
        val root = inflater.inflate(R.layout.fragment_details_users_admin, container, false)

        val Ic_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_DetailsUsers)

        val IMG_User = root.findViewById<ImageView>(R.id.IMG_User_Details_Admin)

        val NombresRecibidos = arguments?.getString("nombres")
        val ApellidosRecibidos = arguments?.getString("apellidos")
        val NombreDeUsuarioRecibidos = arguments?.getString("nombre_usuario")
        val TelefonoRecibidos = arguments?.getString("num_telefono")
        val CorreoRecibidos = arguments?.getString("correo_usuario")
        val ImgRecibida = arguments?.getString("img")
        val RolRecibido = arguments?.getString("Rol")
        var SesionUserRecibida = arguments?.getInt("sesion_user")


        val lbl_Nombres = root.findViewById<TextView>(R.id.lbl_Nombres_DetailsUser_Admin)
        val lbl_Apellidos = root.findViewById<TextView>(R.id.lbl_Apellidos_DetailsUser_Admin)
        val lbl_NombreDeUsu = root.findViewById<TextView>(R.id.lbl_NombreDeUsuario_DetailsUser_Admin)
        val lbl_Telefono = root.findViewById<TextView>(R.id.lbl_Telefono_DetailsUser_Admin)
        val lbl_Correo = root.findViewById<TextView>(R.id.lbl_Correo_DetailsUser_Admin)
        val lbl_Rol = root.findViewById<TextView>(R.id.lbl_RolUser_Admin)
        val lbl_Sesion = root.findViewById<TextView>(R.id.lbl_Sesion_DetailsUser_Admin)

        val EstadoSesion = if (SesionUserRecibida == 0) {
            "Cerrada"
        } else {
            "Abierta"
        }

        lbl_Nombres.text = NombresRecibidos
        lbl_Apellidos.text = ApellidosRecibidos
        lbl_NombreDeUsu.text = NombreDeUsuarioRecibidos
        lbl_Telefono.text = TelefonoRecibidos
        lbl_Correo.text = CorreoRecibidos
        lbl_Rol.text = RolRecibido
        lbl_Sesion.text = EstadoSesion

        Glide.with(IMG_User)
            .load(ImgRecibida)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(IMG_User)

        Ic_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_users_admin)
        }

        return root
    }
}