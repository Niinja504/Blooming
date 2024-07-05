package proyecto.expotecnica.blooming

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import proyecto.expotecnica.blooming.databinding.ActivityDashboardClientBinding

class Dashboard_client : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

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