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
import proyecto.expotecnica.blooming.Employed.ImageViewModel_Employed
import proyecto.expotecnica.blooming.databinding.ActivityDashboardEmployedBinding

class Dashboard_employed : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardEmployedBinding
    private lateinit var imageViewModel: ImageViewModel_Employed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardEmployedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos el ViewModel :)
        imageViewModel = ViewModelProvider(this).get(ImageViewModel_Employed::class.java)

        val navView: BottomNavigationView = binding.navView

        val uuid = intent.getStringExtra("UUID")
        val correo = intent.getStringExtra("Correo")
        if (uuid != null) {
            Log.d("Dashboard_client", "UUID recibido: $uuid")

            val claseConexion = ClaseConexion()
            val mostrarIMG = MostrarIMG(claseConexion)

            CoroutineScope(Dispatchers.Main).launch {
                val urlImagen = mostrarIMG.obtenerImagenUsuario(uuid)

                imageViewModel.setImageUrl(urlImagen)
                imageViewModel.setUuid(uuid)
                imageViewModel.setEmail(correo)

                val bundle = Bundle().apply {
                    putString("URL_IMAGEN", urlImagen)
                    putString("UUID", uuid)
                    putString("CORREO", correo)
                }

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard_employed) as NavHostFragment
                val navController = navHostFragment.navController

                navController.navigate(R.id.navigation_cash_register_employed, bundle)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_employed)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_cash_register_employed, R.id.navigation_inventory_employed, R.id.navigation_orders_employed, R.id.navigation_profile_employed
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_employed)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}