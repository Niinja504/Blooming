package proyecto.expotecnica.blooming.Admin.add_shipping_cost

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.browser.browseractions.BrowserActionsIntent.BrowserActionsItemId
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import oracle.ons.Connection.Status
import proyecto.expotecnica.blooming.R

class AddShippingCost : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_shipping_cost_admin, container, false)
        dialogView = root

        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddShippingCost_Admin)

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shipping_cost_admin)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.MapFragment) as SupportMapFragment?
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.MapFragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        val MapOptions:ImageButton = root.findViewById(R.id.MapOption)
        val popupMenu = PopupMenu(requireContext(), MapOptions)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        MapOptions.setOnClickListener {
            for (item in popupMenu.menu) {
                item.title?.let { title ->
                    val spannableString = SpannableString(title)
                    spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, title.length, 0)
                    item.title = spannableString
                }
            }
            popupMenu.show()
        }

        return root
    }

    private fun changeMap(itemId: Int){
        when(itemId){
            R.id.normal -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.Hibrido -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.Satelite -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        val cityLocation = LatLng(13.709319952929897, -89.2253806653004)
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 10.8f), 200, null)
    }

}
