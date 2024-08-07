package proyecto.expotecnica.blooming

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.MostrarIMG
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.databinding.ActivityDashboardClientBinding

class Dashboard_client : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardClientBinding
    private lateinit var imageViewModel: ImageViewModel_Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el ViewModel
        imageViewModel = ViewModelProvider(this).get(ImageViewModel_Client::class.java)

        val navView: BottomNavigationView = binding.navView

        val uuid = intent.getStringExtra("UUID")
        if (uuid != null) {
            Log.d("Dashboard_client", "UUID recibido: $uuid")

            val claseConexion = ClaseConexion()
            val mostrarIMG = MostrarIMG(claseConexion)

            CoroutineScope(Dispatchers.Main).launch {
                val urlImagen = mostrarIMG.obtenerImagenUsuario(uuid)

                imageViewModel.setImageUrl(urlImagen)

                val bundle = Bundle().apply {
                    putString("URL_IMAGEN", urlImagen)
                }

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard_client) as NavHostFragment
                val navController = navHostFragment.navController

                navController.navigate(R.id.navigation_dashboard_client, bundle)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_client)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard_client, R.id.navigation_shop_client, R.id.navigation_orders_client, R.id.navigation_profile_client
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_client)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}