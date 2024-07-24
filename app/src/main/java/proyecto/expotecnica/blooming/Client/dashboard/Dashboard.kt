package proyecto.expotecnica.blooming.Client.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.MostrarIMG
import proyecto.expotecnica.blooming.R
import java.sql.Connection


class Dashboard : Fragment() {

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("URL_IMAGEN")
            Log.d("Dashboard", "URL de imagen recibida en onCreate: $imageUrl")
        } ?: Log.e("Dashboard", "Argumentos no disponibles en onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_dashboard_client, container, false)
        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Dashboard)

        imageUrl?.let { url ->
            Log.d("Dashboard", "Cargando imagen desde URL: $url")
            Glide.with(IMGUser.context)
                .load(url)
                .placeholder(R.drawable.profile_user)
                .error(R.drawable.profile_user)
                .into(IMGUser)
        } ?: Log.e("Dashboard", "URL de imagen no válida o vacía")

        return root
    }
}