package proyecto.expotecnica.blooming

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import proyecto.expotecnica.blooming.databinding.ActivityDashboardEmployedBinding

class Dashboard_employed : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardEmployedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardEmployedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

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