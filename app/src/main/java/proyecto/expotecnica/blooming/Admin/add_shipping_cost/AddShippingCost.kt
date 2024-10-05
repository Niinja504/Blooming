package proyecto.expotecnica.blooming.Admin.add_shipping_cost

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import java.sql.SQLException
import java.util.UUID

class AddShippingCost : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var Coorde: LatLng? = null
    private lateinit var dialogView: View
    private lateinit var CampoZona: EditText
    private lateinit var CampoCosto: EditText

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

        val mapFragment = childFragmentManager.findFragmentById(R.id.MapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val IC_Regresar = root.findViewById<ImageView>(R.id.Regresar_AddShippingCost_Admin)

        CampoZona = root.findViewById(R.id.txt_NombreZona_AddShippingCost_Admin)
        CampoCosto = root.findViewById(R.id.txt_Costo_AddShippingCost_Admin)
        val Btn_AgregarCosto = root.findViewById<Button>(R.id.btn_Agregar_AddShippingCost)

        CampoZona.filters = arrayOf(InputFilter.LengthFilter(30))
        CampoCosto.filters = arrayOf(InputFilter.LengthFilter(6))

        val mapOptionButton: ImageButton = root.findViewById(R.id.MapOptionsMenu)
        val popupMenu = PopupMenu(requireContext(), mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.bottom_map_options, popupMenu.menu)
        //Bucle en el cual se le cambia el color a lso items
        for (i in 0 until popupMenu.menu.size()) {
            val item = popupMenu.menu.getItem(i)
            val spanString = SpannableString(item.title.toString())
            spanString.setSpan(ForegroundColorSpan(Color.BLACK), 0, spanString.length, 0)
            item.title = spanString
        }

        IC_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_shipping_cost_admin)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        Btn_AgregarCosto.setOnClickListener {
            if (ValidarCamp()){
                INS()
            }
            else{
                Toast.makeText(requireContext(), "Error al añadir costo", Toast.LENGTH_SHORT).show()
            }
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
        val cityLocation = LatLng(13.694048301077249, -89.2167184011952)
        val zoomLevel = 11f

        val cityMarker = mGoogleMap?.addMarker(MarkerOptions().position(cityLocation).title("San Salvador"))

        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, zoomLevel))

        mGoogleMap?.setOnMapLongClickListener { latLng ->
            Coorde = latLng
            showConfirmationDialog(latLng)
        }

        mGoogleMap?.setOnCameraMoveListener {
            val currentZoom = mGoogleMap?.cameraPosition?.zoom ?: zoomLevel
            if (currentZoom > 15f) {
                cityMarker?.isVisible = false
            } else {
                cityMarker?.isVisible = true
            }
        }
    }

    private fun showConfirmationDialog(latLng: LatLng) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar ubicación")
        builder.setMessage("¿Está seguro de agregar esta ubicación?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            Toast.makeText(requireContext(), "Ubicación añadida correctamente", Toast.LENGTH_SHORT).show()
            Coorde = latLng
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(13.694048301077249, -89.2167184011952), 11f))
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun ValidarCamp(): Boolean {
        val Zona = CampoZona.text.toString()
        val Costo = CampoCosto.text.toString()

        var HayErrores = false

        if (Zona.isEmpty()) {
            CampoZona.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoZona.error = null
        }

        if (Costo.isEmpty()) {
            CampoCosto.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoCosto.error = null
        }

        return !HayErrores
    }

    private fun INS() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val ObjConexion = ClaseConexion().CadenaConexion()
                val Add_Address = ObjConexion?.prepareStatement(
                    "INSERT INTO TbCostosEnvio (UUID_CostoEnvio, Nombre_Zona, Costo, Coordernadas_Google) VALUES (?, ?, ?, ?)"
                )!!

                // Verifica el contenido antes de realizar la inserción
                Log.d("CampoZonaText", "CampoZona: ${CampoZona.text.toString()}") // Depuración

                Add_Address.setString(1, UUID.randomUUID().toString())
                Add_Address.setString(2, CampoZona.text.toString())
                val costoText = CampoCosto.text.toString()
                val costo = try {
                    if (costoText.isNotEmpty()) {
                        costoText.toFloat()
                    } else {
                        0.0f
                    }
                } catch (e: NumberFormatException) {
                    0.0f
                }

                Add_Address.setFloat(3, costo)
                Add_Address.setString(4, Coorde?.toString() ?: "")
                try {
                    Add_Address.executeUpdate()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Se ha añadido exitosamente", Toast.LENGTH_LONG).show()
                        Limpiar()
                        findNavController().navigate(R.id.navigation_shipping_cost_admin)
                    }
                } catch (e: SQLException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al añadir el costo: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun Limpiar(){
        CampoZona.text.clear()
        CampoCosto.text.clear()
    }
}