package proyecto.expotecnica.blooming.Admin.notifications

import DataC.Data_Notifications
import RecyclerViewHelpers.Adaptador_Notifications_Admin
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

class Notifications : Fragment() {
    private val imageViewModel: ImageViewModel_Admin by activityViewModels()
    private var miAdaptador: Adaptador_Notifications_Admin? = null
    private lateinit var Buscador: EditText
    //Handler se utiliza para ejecutar la funcion runnable cada 5 segundos =)
    private val handler = Handler(Looper.getMainLooper())
    //Dentro de esta variable se agrupara el codigo que quiero que se ejecute cada 5 segundos =)
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_notifications_admin, container, false)

        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Notifications)
        val RCV_Notifications = root.findViewById<RecyclerView>(R.id.RCV_Notifications_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Notifications.layoutManager = LinearLayoutManager(requireContext())
        Buscador = root.findViewById(R.id.txt_Buscar_Notifications_Admin)
        val LimpiarBuscador = root.findViewById<ImageView>(R.id.IC_Limpiar_Bucador_Notifications_Admin)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        LimpiarBuscador.setOnClickListener {
            Limpiar()
            Teclado()
        }

        suspend fun MostrarDatos(): List<Data_Notifications> {
            // 1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            // 2- Preparo la consulta
            val query = "SELECT * FROM TbNotificaciones WHERE UUID_User = ?"
            val preparedStatement = objConexion?.prepareStatement(query)

            // 3- Establezco el parámetro de búsqueda
            preparedStatement?.setString(1, imageViewModel.uuid.value)

            // 4- Ejecutar la consulta
            val ResultSet = preparedStatement?.executeQuery()!!

            // Voy a guardar todo lo que me traiga el Select
            val Notifications = mutableListOf<Data_Notifications>()

            while (ResultSet.next()) {
                val Titulo = ResultSet.getString("Titulo") ?: "Título no disponible"
                val Mensaje = ResultSet.getString("Mensaje") ?: "Mensaje no disponible"
                val Tiempo = ResultSet.getString("Tiempo_Envio") ?: "Tiempo no disponible"
                val Fecha = ResultSet.getString("Fecha_Envio") ?: "Fecha no disponible"
                val uuid = ResultSet.getString("UUID_Notificacion") ?: "UUID no disponible"
                val UUID_user = ResultSet.getString("UUID_User") ?: "Usuario no disponible"

                val Notification = Data_Notifications(uuid, UUID_user, Titulo, Mensaje, Tiempo, Fecha)
                Notifications.add(Notification)
            }

            return Notifications
        }

        //Esta funcion nos permite mostrar y actualizar cada 5 segundos los mensajes
        runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    val NotificationsDB = MostrarDatos()
                    withContext(Dispatchers.Main) {
                        miAdaptador = Adaptador_Notifications_Admin(NotificationsDB)
                        RCV_Notifications.adapter = miAdaptador
                    }
                }
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(runnable)

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

    fun Teclado() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentView = activity?.currentFocus
        currentView?.clearFocus()
        (view as? View)?.let { v ->
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}