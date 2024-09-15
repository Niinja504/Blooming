package RecyclerViewHelpers

import DataC.DataShippingCost_Admin
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Adaptador_ShippingCost_Admin  (private val context: Context, var Datos: List<DataShippingCost_Admin>): RecyclerView.Adapter<ViewHolder_ShippingCost_Admin>(),
    OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var Coorde: LatLng? = null
    private var datosFiltrados = Datos

    fun ActualizarListaDespuesDeEditar(uuid: String, NuevaZona: String, NuevoCosto: Float, NuevaCoordenada: String){
        val Index = Datos.indexOfFirst { it.UUID == uuid }
        Datos[Index].Nombre = NuevaZona
        Datos[Index].CostoEnvio = NuevoCosto
        Datos[Index].Coorderna = NuevaCoordenada
        notifyItemChanged(Index)
    }

    fun eliminarDatos(nombreZona: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- creo una variable que contenga
            //un PrepareStatement
            val DeleteUser = objConexion?.prepareStatement("DELETE TbCostosEnvio WHERE UUID_CostoEnvio = ?")!!
            DeleteUser.setString(1, nombreZona)
            DeleteUser.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarDato(
        nombreZona: String,
        costoEnvio: Float,
        coordenadas: LatLng?,
        uuid: String,
        scope: CoroutineScope
    ) {
        val coordenadasStr = coordenadas?.let { "${it.latitude},${it.longitude}" } ?: "No definido"
        scope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val UpdateCosto = ObjConexion?.prepareStatement(
                "UPDATE TbCostosEnvio SET Nombre_Zona = ?, Costo = ?, Coordernadas_Google = ? WHERE UUID_CostoEnvio = ?"
            )
            UpdateCosto?.apply {
                setString(1, nombreZona)
                setFloat(2, costoEnvio)
                setString(3, coordenadasStr)
                setString(4, uuid)
                executeUpdate()
            }

            withContext(Dispatchers.Main){
                ActualizarListaDespuesDeEditar(uuid, nombreZona, costoEnvio, coordenadasStr)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_ShippingCost_Admin {
        val context = parent.context
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(context).inflate(R.layout.activity_card_shipping_cost_admin, parent, false)
        return ViewHolder_ShippingCost_Admin(vista)
    }


    override fun getItemCount() = datosFiltrados.size

    override fun onBindViewHolder(holder: ViewHolder_ShippingCost_Admin, position: Int) {
        //Poder darle clic a la elemento de la card
        val item = datosFiltrados[position]
        holder.Nombre_Zona.text = item.Nombre
        holder.Costo_Envio.text = item.CostoEnvio.toString()


        //Todo: clic al icono de borrar
        holder.IC_Delete.setOnClickListener {
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto

            val context = holder.itemView.context

            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                eliminarDatos(item.UUID, position)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //TODO: ClIC AL ICONO DE EDITAR
        holder.IC_Editar.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.update_costo_envio, null)

            val mapFragment = (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.MapFragment_Update) as? SupportMapFragment
            mapFragment?.getMapAsync(this)

            val mapOptionButton: ImageButton = dialogView.findViewById(R.id.MapOptionsMenu_Update)
            val popupMenu = PopupMenu(context, mapOptionButton)
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

            val Update_Zona = dialogView.findViewById<EditText>(R.id.Txt_Zona)
            val Update_Costo = dialogView.findViewById<EditText>(R.id.Txt_Costo)


            Update_Zona.filters = arrayOf(InputFilter.LengthFilter(20))
            Update_Costo.filters = arrayOf(InputFilter.LengthFilter(5))

            Update_Zona.setText(item.Nombre)
            Update_Costo.setText(item.CostoEnvio.toString())

            val builder = AlertDialog.Builder(context)
            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar") { dialog, _ ->
                val nombreZona = Update_Zona.text.toString()
                val costoEnvio = Update_Costo.text.toString().toFloat()
                val coordenadas = Coorde

                actualizarDato(nombreZona, costoEnvio, coordenadas, item.UUID, GlobalScope)

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("NombreZona", item.Nombre)
                putFloat("CostoEnvio", item.CostoEnvio)
                putString("Coordenadas", item.Coorderna)
            }
            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Shipping_Offers, bundle)
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
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar ubicación")
        builder.setMessage("¿Está seguro de agregar esta ubicación?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            Toast.makeText(context, "Ubicación añadida correctamente", Toast.LENGTH_SHORT).show()
            Coorde = latLng
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(13.694048301077249, -89.2167184011952), 11f))
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    fun filtrar(texto: String) {
        datosFiltrados = if (texto.isEmpty()) {
            Datos
        } else {
            Datos.filter {
                it.Nombre.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}