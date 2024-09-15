package proyecto.expotecnica.blooming.Admin.details

import android.graphics.Color
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import proyecto.expotecnica.blooming.R

class Details_Shipping : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var coordenadasRecibida: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            coordenadasRecibida = it.getString("Coordenadas")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_details_shipping_cost_admin, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.MapFragment_Shipping_Admin) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val Ic_Regresar = root.findViewById<ImageView>(R.id.IC_Regresar_Details_ShippingCost)

        val ZonaRecibida = arguments?.getString("NombreZona")
        val CostoRecibido = arguments?.getFloat("CostoEnvio")

        val mapOptionButton: ImageButton = root.findViewById(R.id.MapOptionsMenuShipping)
        val popupMenu = PopupMenu(requireContext(), mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.bottom_map_options, popupMenu.menu)
        for (i in 0 until popupMenu.menu.size()) {
            val item = popupMenu.menu.getItem(i)
            val spanString = SpannableString(item.title.toString())
            spanString.setSpan(ForegroundColorSpan(Color.BLACK), 0, spanString.length, 0)
            item.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        val lbl_Zona = root.findViewById<TextView>(R.id.lbl_Zona_Details_ShippingCost_Admin)
        val lbl_Costo = root.findViewById<TextView>(R.id.lbl_Costo_Details_ShippingCost_Admin)

        lbl_Zona.text = ZonaRecibida
        lbl_Costo.text = CostoRecibido.toString()

        Ic_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shipping_cost_admin)
        }

        return root
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.Normal -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.Hibrido -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.Satelite -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.Terreno -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        coordenadasRecibida?.let {
            Log.d("Details_Shipping", "Coordenadas recibidas: $it")

            val coordenadasArray = it.split(",")
            if (coordenadasArray.size == 2) {
                val latitud = coordenadasArray[0].toDoubleOrNull()
                val longitud = coordenadasArray[1].toDoubleOrNull()

                if (latitud != null && longitud != null) {
                    Log.d("Details_Shipping", "Coordenadas válidas: lat=$latitud, lon=$longitud")

                    val cityLocation = LatLng(latitud, longitud)
                    val initialZoomLevel = 11f
                    val finalZoomLevel = 15f

                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, initialZoomLevel))
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, finalZoomLevel), 2000, null)

                    val cityMarker = mGoogleMap?.addMarker(
                        MarkerOptions()
                            .position(cityLocation)
                            .title(arguments?.getString("NombreZona"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )

                    mGoogleMap?.setOnCameraMoveListener {
                        val currentZoom = mGoogleMap?.cameraPosition?.zoom ?: initialZoomLevel
                        cityMarker?.isVisible = currentZoom <= 15f
                    }
                } else {
                    Log.e("Details_Shipping", "Coordenadas inválidas: lat=$latitud, lon=$longitud")
                }
            } else {
                Log.e("Details_Shipping", "Formato de coordenadas incorrecto: $it")
            }
        } ?: run {
            Log.e("Details_Shipping", "Coordenadas no recibidas")
        }
    }
}

