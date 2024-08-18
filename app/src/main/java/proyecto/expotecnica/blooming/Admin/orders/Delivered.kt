package proyecto.expotecnica.blooming.Admin.orders

import DataC.DataOrdersDelivered_Admin
import RecyclerViewHelpers.Adaptador_OrdersDelivered_Admin
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Delivered : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders_delivered_admin, container, false)

        // Variables que se van a utilizar
        val RCV_Pedido = root.findViewById<RecyclerView>(R.id.RCV_OrderDelivered_Admin)
        RCV_Pedido.layoutManager = LinearLayoutManager(requireContext())

        // Llamar a la función de mostrar datos dentro de una corutina
        CoroutineScope(Dispatchers.IO).launch {
            val UsuarioDB = MostrarDatos()
            withContext(Dispatchers.Main) {
                // Verificar que la lista de datos no esté vacía antes de asignar el adaptador
                if (UsuarioDB.isNotEmpty()) {
                    val miAdaptador = Adaptador_OrdersDelivered_Admin(UsuarioDB)
                    RCV_Pedido.adapter = miAdaptador
                } else {
                    Log.w("DeliveredOrdersFragment", "No se encontraron datos para mostrar.")
                }
            }
        }

        return root
    }

    private suspend fun MostrarDatos(): List<DataOrdersDelivered_Admin> {
        val objConexion = ClaseConexion().CadenaConexion() ?: return emptyList()
        val statement = objConexion.createStatement() ?: return emptyList()

        val query = """
            SELECT 
            p.UUID_Pedido,
            p.UUID_Cliente,
            p.Fecha_Venta,
            p.Hora_Venta,
            p.Subtotal,
            pr.UUID_Producto,
            pr.Precio_Producto,
            pr.Cantidad_Producto,
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
        """
        val resultSet = statement.executeQuery(query) ?: return emptyList()

        val pedidos = mutableListOf<DataOrdersDelivered_Admin>()
        while (resultSet.next()) {
            val pedido = DataOrdersDelivered_Admin(
                resultSet.getString("UUID_Pedido"),
                resultSet.getString("UUID_Cliente"),
                resultSet.getString("Fecha_Venta"),
                resultSet.getString("Hora_Venta"),
                resultSet.getFloat("Subtotal"),
                resultSet.getString("UUID_Producto"),
                resultSet.getFloat("Precio_Producto"),
                resultSet.getInt("Cantidad_Producto"),
                resultSet.getString("FechaEntrega"),
                resultSet.getString("HorarioEntrega"),
                resultSet.getString("NombreCliente"),
                resultSet.getString("NombreCalle"),
                resultSet.getString("LugarEntrega"),
                resultSet.getString("Colonia"),
                resultSet.getString("Coordenadas"),
                resultSet.getString("SinMensaje"),
                resultSet.getString("Dedicatoria"),
                resultSet.getString("EnvioSinNombre"),
                resultSet.getString("NombreEmisor")
            )
            pedidos.add(pedido)
        }
        return pedidos
    }
}