package proyecto.expotecnica.blooming.Admin.users

import DataC.DataUsers
import RecyclerViewHelpers.Adaptador_Users_Admin
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Admin.ImageViewModel_Admin
import proyecto.expotecnica.blooming.R

class Users : Fragment() {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
    private var miAdaptador: Adaptador_Users_Admin? = null
    private lateinit var Buscador: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_users_admin, container, false)

        val Agregar = root.findViewById<Button>(R.id.btn_AddUsers_Admin)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Users)
        Buscador = root.findViewById(R.id.txt_Buscar_Users)
        val LimpiarBuscador = root.findViewById<ImageView>(R.id.IC_Limpiar_Bucador_Users)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(url)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        val RCV_Users = root.findViewById<RecyclerView>(R.id.RCV_AddUser_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Users.layoutManager = LinearLayoutManager(requireContext())

        LimpiarBuscador.setOnClickListener {
            Limpiar()
        }

        Agregar.setOnClickListener {
            findNavController().navigate(R.id.AddUsers_admin)
        }

        suspend fun MostrarDatos(): List<DataUsers> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM TbUsers")!!

            //Voy a guardar all lo que me traiga el Select

            val Usuarios = mutableListOf<DataUsers>()

            while (resultSet.next()){
                val Nombre = resultSet.getString("Nombres_User")
                val Apellido = resultSet.getString("Apellido_User")
                val NombreUsuario = resultSet.getString("Nombre_de_Usuario")
                val Telefono = resultSet.getString("Num_Telefono_User")
                val Edad = resultSet.getInt("Edad_User")
                val Correo = resultSet.getString("Email_User")
                val Contra = resultSet.getString("Contra_User")
                val IMG_User = resultSet.getString("Img_User")
                val Rol = resultSet.getString("Rol_User")
                val Sesion = resultSet.getInt("Sesion_User")
                val uuid = resultSet.getString("UUID_User")
                val Usuario = DataUsers(uuid, Nombre, Apellido, NombreUsuario, Telefono, Edad, Correo, Contra, IMG_User,Rol , Sesion)
                Usuarios.add(Usuario)
            }
            return Usuarios
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val UsuarioDB = MostrarDatos()
            withContext(Dispatchers.Main){
                miAdaptador = Adaptador_Users_Admin(UsuarioDB)
                RCV_Users.adapter = miAdaptador
            }
        }

        //Buscador que funciona por medio del nombre =)
        Buscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                miAdaptador?.filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }

    fun Limpiar(){
        Buscador.text.clear()
        Buscador.clearFocus()
    }
}