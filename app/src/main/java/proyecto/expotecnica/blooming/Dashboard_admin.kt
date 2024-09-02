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
import proyecto.expotecnica.blooming.Admin.ImageViewModel_Admin
import proyecto.expotecnica.blooming.databinding.ActivityDashboardAdminBinding

class Dashboard_admin : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var imageViewModel: ImageViewModel_Admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel_Admin::class.java)

        val navView: BottomNavigationView = binding.navView

        val uuid = intent.getStringExtra("UUID")
        if (uuid != null){
            Log.d("Dashboard_Admin", "UUID recibido: $uuid")

            val claseConexion = ClaseConexion()
            val mostrarIMG = MostrarIMG(claseConexion)

            CoroutineScope(Dispatchers.Main).launch {
                val urlImagen = mostrarIMG.obtenerImagenUsuario(uuid)

                imageViewModel.setImageUrl(urlImagen)

                val bundle = Bundle().apply {
                    putString("URL_IMAGEN", urlImagen)
                }

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard_admin) as NavHostFragment
                val navController = navHostFragment.navController

                navController.navigate(R.id.navigation_users_admin, bundle)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_admin)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_users_admin, R.id.navigation_inventory_admin, R.id.navigation_orders_admin, R.id.navigation_profile_admin
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_admin)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}