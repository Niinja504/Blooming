package proyecto.expotecnica.blooming

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import proyecto.expotecnica.blooming.databinding.ActivityDashboardAdminBinding

class Dashboard_admin : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_admin)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_employed_admin, R.id.navigation_inventory_admin, R.id.navigation_orders_admin, R.id.navigation_profile_admin
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard_admin)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}