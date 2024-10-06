package proyecto.expotecnica.blooming.Client.delivery_address

import DataC.Data_Address_Admin
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


class DeliveryAddress : Fragment(), OnMapReadyCallback {
    private var selected: Data_Address_Admin? = null
    private var mGoogleMap: GoogleMap? = null
    private var Coorde: LatLng? = null
    private var UUID_PedidoR: String? = null
    private var Costo_Pedido_Cliente: Float? = null
    private var LugarPedido: String? = null
    private var Costo: Float? = null
    private lateinit var dialogView: View
    private lateinit var CampoNombreCliente: EditText
    private lateinit var CampoNombreCalle: EditText
    companion object{
        fun newInstance(
            UUID_Pedido: String,
            Costo_Venta: Float
        ): DeliveryAddress {
            val fragment = DeliveryAddress ()
            val args = Bundle()
            args.putString("UUID_Pedido", UUID_Pedido)
            args.putFloat("Costo_Pedido", Costo_Venta)
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

        lifecycleScope.launch {
            val items = ZonaPedido()
            setupAutoCompleteTextView(root, items)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.MapFragmentDelivery) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        UUID_PedidoR = arguments?.getString("UUID_Pedido")
        Costo_Pedido_Cliente = arguments?.getFloat("Costo_Pedido")
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_Delivery_Address_client)

        CampoNombreCliente = root.findViewById(R.id.txt_Nombre_DireEntrega_Pedido)
        CampoNombreCalle = root.findViewById(R.id.txt_Calle_DireEntrega_Pedido)

        val Switch = root.findViewById<MaterialSwitch>(R.id.SW_EntregaLocal)
        val Add_Address = root.findViewById<Button>(R.id.btn_Agregar_DeliveryAddress_client)

        CampoNombreCliente.filters = arrayOf(InputFilter.LengthFilter(20))
        CampoNombreCalle.filters = arrayOf(InputFilter.LengthFilter(50))

        Switch.isChecked = false

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.delivered_date)
        }

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

    private fun setupAutoCompleteTextView(root: View, items: List<Data_Address_Admin>) {
        val autoComplete: AutoCompleteTextView = root.findViewById(R.id.autoComplete_DeliveryAddress_Admin)
        val adaptador = ArrayAdapter(requireContext(), R.layout.list_item, items.map { it.nombreZona})
        autoComplete.setAdapter(adaptador)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, position, _ ->
            selected = items[position]
            selected?.uuid
            Toast.makeText(requireContext(), "Colonia: ${selected?.nombreZona}", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun ZonaPedido(): List<Data_Address_Admin> {
        return withContext(Dispatchers.IO) {
            val zonaPedidos = mutableListOf<Data_Address_Admin>()

            val conexion = ClaseConexion().CadenaConexion()
            conexion?.use { conn ->
                try {
                    val query = "SELECT UUID_CostoEnvio, Nombre_Zona, Costo FROM TbCostosEnvio"
                    val statement = conn.createStatement()
                    val resultSet = statement.executeQuery(query)

                    while (resultSet.next()) {
                        val uuid = resultSet.getString("UUID_CostoEnvio")
                        val nombreZona = resultSet.getString("Nombre_Zona")
                        Costo = resultSet.getFloat("Costo")
                        zonaPedidos.add(Data_Address_Admin(uuid, nombreZona, Costo))
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
            zonaPedidos
        }
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
                Add_Address.setString(5, selected?.uuid ?: "No seleccionado")
                Add_Address.setString(6, Coorde?.toString() ?: "No especificado")
                Add_Address.executeUpdate()

                val costoEnvio = Costo ?: 0f
                val costoPedidoCliente = Costo_Pedido_Cliente ?: 0f
                val SumaToria = costoEnvio + costoPedidoCliente

                val Add_Envio = ObjConexion?.prepareStatement("UPDATE TbPedido_Cliente SET Costo_Envio = ? WHERE UUID_Pedido = ?")!!
                Add_Envio.setFloat(1, Costo.toString().toFloat())
                Add_Envio.setString(2, UUID_PedidoR)
                Add_Envio.executeUpdate()


                val Costo_Final = ObjConexion?.prepareStatement("UPDATE TbPedido_Cliente SET Total = ? WHERE UUID_Pedido = ?")!!
                Costo_Final.setFloat(1, SumaToria)
                Costo_Final.setString(2, UUID_PedidoR)
                Costo_Final.executeUpdate()


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
    }
}