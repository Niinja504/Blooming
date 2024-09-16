package proyecto.expotecnica.blooming.Employed.orders

import DataC.Data_Orders
import RecyclerViewHelpers.Adaptador_Orders_Employed
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Employed.ImageViewModel_Employed
import proyecto.expotecnica.blooming.R

class Orders : Fragment() {
    private val imageViewModel: ImageViewModel_Employed by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders_employed, container, false)

        val RCV_Pedido = root.findViewById<RecyclerView>(R.id.RCV_Orders_Employed)
        RCV_Pedido.layoutManager = LinearLayoutManager(requireContext())

        val Ic_Sales = root.findViewById<ImageView>(R.id.Ic_Sales_Orders_Employed)
        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Pedidos_Employed)
        val toggleGroup = root.findViewById<MaterialButtonToggleGroup>(R.id.toggleButton_Employed)
        val btnPedidosPendientes = root.findViewById<MaterialButton>(R.id.btn_Pedidos_Pendientes_Employed)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        Ic_Sales.setOnClickListener {
            findNavController().navigate(R.id.navigation_sales_employed)
        }

        toggleGroup.check(btnPedidosPendientes.id)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    val pedidoPendiente = when (checkedId) {
                        R.id.btn_Pedidos_Pendientes_Employed -> "Si"
                        R.id.btn_Pedidos_Entregados_Employed -> "No"
                        else -> return@launch
                    }
                    val productosDB = MostrarDatos(pedidoPendiente)
                    withContext(Dispatchers.Main) {
                        val miAdaptador = Adaptador_Orders_Employed(productosDB)
                        RCV_Pedido.adapter = miAdaptador
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = MostrarDatos("Si")
            withContext(Dispatchers.Main) {
                if (productosDB.isNotEmpty()) {
                    val miAdaptador = Adaptador_Orders_Employed(productosDB)
                    RCV_Pedido.adapter = miAdaptador
                } else {
                    Log.w("OrdersFragment", "No se encontraron datos para mostrar.")
                }
            }
        }

        return root

    }

    private suspend fun MostrarDatos(pedidoPendiente: String): List<Data_Orders> {
        val objConexion = ClaseConexion().CadenaConexion() ?: return emptyList()
        val preparedStatement = objConexion.prepareStatement(
            """
        SELECT 
        p.UUID_Pedido AS uuid,
        p.UUID_Cliente AS uuid_Cliente,
        p.Fecha_Venta AS FechaVenta,
        p.Hora_Venta AS HoraVenta,
        p.Subtotal AS SubTotal,
        p.Pedido_Pendiente AS Pendiente,
        pr.UUID_Producto AS UUID_Producto,
        pr.Precio_Producto AS PrecioProducto,
        pr.Cantidad_Producto AS CantidadProducto,
        h.Fecha AS FechaEntrega,
        h.Horario AS HorarioEntrega,
        d.Nombre_Cliente AS NombreCliente,
        d.Nombre_Calle AS NombreCalle,
        d.Lugar_Entrega AS LugarEntrega,
        d.Colonia AS Colonia,
        d.Coordenadas_Google AS Coordenadas,
        de.Sin_Mensaje AS SinMensaje,
        de.Dedicatoria AS Dedicatoria,
        de.Envio_Sin_Nombre AS EnvioSinNombre,
        de.Nombre_Emisor AS NombreEmisor
        FROM TbPedido_Cliente p
        LEFT JOIN TbProductosPedido pr ON p.UUID_Pedido = pr.UUID_Pedido
        LEFT JOIN TbHorarioPedido h ON p.UUID_Pedido = h.UUID_Pedido
        LEFT JOIN TbDireccionPedido d ON p.UUID_Pedido = d.UUID_Pedido
        LEFT JOIN TbDedicatorias de ON p.UUID_Pedido = de.UUID_Pedido
        WHERE p.Pedido_Pendiente = ?
    """
        )
        preparedStatement.setString(1, pedidoPendiente)
        val resultSet = preparedStatement.executeQuery() ?: return emptyList()

        val pedidos = mutableListOf<Data_Orders>()
        while (resultSet.next()) {
            val pedido = Data_Orders(
                uuid = resultSet.getString("uuid") ?: "",
                uuid_Cliente = resultSet.getString("uuid_Cliente") ?: "",
                FechaVenta = resultSet.getString("FechaVenta") ?: "",
                HoraVenta = resultSet.getString("HoraVenta") ?: "",
                SubTotal = resultSet.getFloat("SubTotal"),
                Pendiente = resultSet.getString("Pendiente") ?: "",
                UUID_Producto = resultSet.getString("UUID_Producto") ?: "",
                PrecioProducto = resultSet.getFloat("PrecioProducto"),
                CantidadProducto = resultSet.getInt("CantidadProducto"),
                FechaEntrega = resultSet.getString("FechaEntrega") ?: "",
                HorarioEntrega = resultSet.getString("HorarioEntrega") ?: "",
                NombreCliente = resultSet.getString("NombreCliente") ?: "",
                NombreCalle = resultSet.getString("NombreCalle") ?: "",
                LugarEntrega = resultSet.getString("LugarEntrega") ?: "",
                Colonia = resultSet.getString("Colonia") ?: "",
                Coordenadas = resultSet.getString("Coordenadas") ?: "",
                SinMensaje = resultSet.getString("SinMensaje") ?: "",
                Dedicatoria = resultSet.getString("Dedicatoria") ?: "",
                EnvioSinNombre = resultSet.getString("EnvioSinNombre") ?: "",
                NombreEmisor = resultSet.getString("NombreEmisor") ?: ""
            )
            pedidos.add(pedido)
        }
        return pedidos
    }
}