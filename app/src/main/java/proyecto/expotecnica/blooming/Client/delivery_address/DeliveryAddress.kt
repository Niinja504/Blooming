package proyecto.expotecnica.blooming.Client.delivery_address

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R


class DeliveryAddress : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var Coorde: LatLng? = null
    private var UUID_PedidoR: String? = null
    private var LugarPedido: String? = null
    private lateinit var dialogView: View
    private lateinit var CampoNombreCliente: EditText
    private lateinit var CampoNombreCalle: EditText
    private lateinit var CampoColonia: EditText
    companion object{
        fun newInstance(
            UUID_Pedido: String
        ): DeliveryAddress {
            val fragment = DeliveryAddress ()
            val args = Bundle()
            args.putString("UUID_Pedido", UUID_Pedido)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_delivery_address_client, container, false)
        dialogView = root

        val mapFragment = childFragmentManager.findFragmentById(R.id.MapFragmentDelivery) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        UUID_PedidoR = arguments?.getString("UUID_Pedido")

        CampoNombreCliente = root.findViewById(R.id.txt_Nombre_DireEntrega_Pedido)
        CampoNombreCalle = root.findViewById(R.id.txt_Calle_DireEntrega_Pedido)
        CampoColonia = root.findViewById(R.id.txt_Colonia_DireEntrega_Pedido)
        val Switch = root.findViewById<MaterialSwitch>(R.id.SW_EntregaLocal)
        val Add_Address = root.findViewById<Button>(R.id.btn_Agregar_DeliveryAddress_client)

        CampoNombreCliente.filters = arrayOf(InputFilter.LengthFilter(20))
        CampoNombreCalle.filters = arrayOf(InputFilter.LengthFilter(50))
        CampoColonia.filters = arrayOf(InputFilter.LengthFilter(40))

        Switch.isChecked = false

        val mapOptionButton: ImageButton = root.findViewById(R.id.MapOptionsMenuDelivery)
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

        Switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showNombreClientDialog()
                LugarPedido = "Se entregara en el local"

            } else {
                LugarPedido = "Se entregara a domicilio"
            }
        }

        Add_Address.setOnClickListener {
            if (Switch.isChecked) {
                Toast.makeText(requireContext(), "Por favor introduzca solo su nombre", Toast.LENGTH_LONG).show()
            } else {
                if (ValidarCamp()) {
                    INS()

                } else {
                    Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_LONG).show()
                }
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
        val NombreCliente = CampoNombreCliente.text.toString()
        val NombreCalle = CampoNombreCalle.text.toString()
        val NombreColonia = CampoColonia.text.toString()

        var HayErrores = false

        if (NombreCliente.isEmpty()) {
            CampoNombreCliente.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombreCliente.error = null
        }

        if (NombreCalle.isEmpty()) {
            CampoNombreCalle.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombreCalle.error = null
        }

        if (NombreColonia.isEmpty()) {
            CampoColonia.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoColonia.error = null
        }

        return !HayErrores
    }

    private fun showNombreClientDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.ven_nombre_pedido_cliente, null)

        dialogBuilder.setView(dialogView)

        val inputName = dialogView.findViewById<EditText>(R.id.txt_NombreClient_Pedido_Address_Client)

        dialogBuilder
            .setTitle("Introduce tu nombre")
            .setPositiveButton("Aceptar") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                } else {
                    CampoNombreCliente.setText(name)
                    INS()
                    Toast.makeText(requireContext(), "Nombre recibido: $name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun INS(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val ObjConexion = ClaseConexion().CadenaConexion()
                val Add_Address = ObjConexion?.prepareStatement(
                    "INSERT INTO TbDireccionPedido (UUID_Pedido, Nombre_Cliente, Nombre_Calle, Lugar_Entrega, Colonia, Coordenadas_Google) VALUES (?, ?, ?, ?, ?, ?)"
                )!!

                Add_Address.setString(1, UUID_PedidoR)
                Add_Address.setString(2, CampoNombreCliente.text.toString())
                Add_Address.setString(3, CampoNombreCalle.text.toString())
                Add_Address.setString(4, LugarPedido ?: "Sin especificar")
                Add_Address.setString(5, CampoColonia.text.toString())
                Add_Address.setString(6, Coorde.toString())
                Add_Address.executeUpdate()
            }
            val bundle = Bundle().apply {
                putString("UUID_Pedido", UUID_PedidoR)
            }
            findNavController().navigate(R.id.action_Dedication, bundle)
            Toast.makeText(requireContext(), "Datos de entrega añadidos", Toast.LENGTH_LONG).show()
            LimpiarCampos()
        }
    }

    private fun LimpiarCampos(){
        CampoNombreCliente.text.clear()
        CampoNombreCalle.text.clear()
        CampoColonia.text.clear()
    }
}