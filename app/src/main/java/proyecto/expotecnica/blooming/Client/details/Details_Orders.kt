package proyecto.expotecnica.blooming.Client.details

import DataC.Data_Productos
import RecyclerViewHelpers.Adaptador_Product_Details
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Admin.details.Details_Orders
import proyecto.expotecnica.blooming.R

class Details_Orders : Fragment() {
    private lateinit var productos: List<Data_Productos>
    private lateinit var CampoNota: EditText
    companion object{
        fun newInstance(
            UUID: String,
            HoraVenta: String,
            SubTotal: Float,
            Pendiente: String,
            UUID_Producto: String,
            PrecioProducto: Float,
            CantidadProducto: Int,
            FechaEntrega: String,
            HorarioEntrega: String,
            NombreCliente: String,
            NombreCalle: String,
            LugarEntrega: String,
            Colonia: String,
            Coordenadas: String,
            SinMensaje: String,
            Dedicatoria: String,
            EnvioSinNombre: String,
            NombreEmisor: String,
            Productos: List<Data_Productos>
        ): Details_Orders {
            val fragment = Details_Orders()
            val args = Bundle()
            args.putString("uuid", UUID)
            args.putString("HoraVenta", HoraVenta)
            args.putFloat("SubTotal", SubTotal)
            args.putString("Pendiente", Pendiente)
            args.putString("UUID_Producto", UUID_Producto)
            args.putFloat("Precio_Producto", PrecioProducto)
            args.putInt("Cantidad_Producto", CantidadProducto)
            args.putString("Fecha_Entrega", FechaEntrega)
            args.putString("Horario_Entrega", HorarioEntrega)
            args.putString("Nombre_Cliente", NombreCliente)
            args.putString("Nombre_Calle", NombreCalle)
            args.putString("Lugar_Entrega", LugarEntrega)
            args.putString("Colonia", Colonia)
            args.putString("Coordenadas", Coordenadas)
            args.putString("Sin_Mensaje", SinMensaje)
            args.putString("Dedicatoria", Dedicatoria)
            args.putString("Envio_Sin_Nombre", EnvioSinNombre)
            args.putString("Nombre_Emisor", NombreEmisor)
            args.putParcelableArrayList("productos", ArrayList(Productos))
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
        val root = inflater.inflate(R.layout.fragment_details_orders_client, container, false)

        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_Orders_Client)

        val RCV_Pedido = root.findViewById<RecyclerView>(R.id.RCV_Detalles_Pedido_Client)
        RCV_Pedido.layoutManager = LinearLayoutManager(requireContext())

        arguments?.let {
            productos = it.getParcelableArrayList("productos") ?: emptyList()
        }

        val uuid_Recibido = arguments?.getString("uuid")
        val Hora_Venta_Recibida = arguments?.getString("HoraVenta")
        val SubTotal_Recibido = arguments?.getFloat("SubTotal")
        val Pendiente_Recibido = arguments?.getString("Pendiente")
        val UUID_Producto_Recibida = arguments?.getString("UUID_Producto")
        val Precio_Producto_Recibido = arguments?.getFloat("Precio_Producto")
        val Cantidad_Producto_Recibida = arguments?.getInt("Cantidad_Producto")
        val Fecha_Entrega_Recibida = arguments?.getString("Fecha_Entrega")
        val Horario_Entrega_Recibido = arguments?.getString("Horario_Entrega")
        val Nombre_Cliente_Recibido = arguments?.getString("Nombre_Cliente")
        val Nombre_Calle_Recibido = arguments?.getString("Nombre_Calle")
        val Lugar_Entrega_Recibido = arguments?.getString("Lugar_Entrega")
        val Colonia_Recibida = arguments?.getString("Colonia")
        val Coordenadas_Recibidas = arguments?.getString("Coordenadas")
        val Sin_Mensaje_Recibido = arguments?.getString("Sin_Mensaje")
        val Dedicatoria_Recibida = arguments?.getString("Dedicatoria")
        val Envio_Sin_Nombre_Recibido = arguments?.getString("Envio_Sin_Nombre")
        val Nombre_Emisor_Recibido  = arguments?.getString("Nombre_Emisor")

        val lbl_Nombre_Cliente = root.findViewById<TextView>(R.id.lbl_NombreCliente_Details_Orders_Client)
        val lbl_Fecha_Entrega = root.findViewById<TextView>(R.id.lbl_Fecha_Entrega_Details_orders_Client)
        val lbl_Hora_Entrega = root.findViewById<TextView>(R.id.lbl_Hora_Entrega_Details_orders_Client)
        val lbl_Direccion_Entrega = root.findViewById<TextView>(R.id.lbl_Direccion_Details_Orders_Client)
        val lbl_Dedicatoria = root.findViewById<TextView>(R.id.lbl_Dedicatoria_Orders_Client)
        val lbl_SubTotal = root.findViewById<TextView>(R.id.lbl_SubTotal_Details_Orders_Client)
        CampoNota = root.findViewById(R.id.txt_Nota_details_Orders_Client)
        val btn_Eliminar_Pedido = root.findViewById<Button>(R.id.btn_EliminarVenta_Details_Client)
        val btn_Confirmar_Venta = root.findViewById<Button>(R.id.btn_ConfirmarVenta_Details_Client)

        lbl_Nombre_Cliente.text = Nombre_Cliente_Recibido
        lbl_Fecha_Entrega.text = Fecha_Entrega_Recibida
        lbl_Hora_Entrega.text = Horario_Entrega_Recibido
        lbl_Direccion_Entrega.text = Lugar_Entrega_Recibido
        lbl_SubTotal.text = SubTotal_Recibido.toString()
        lbl_Dedicatoria.text = Sin_Mensaje_Recibido

        val adaptador = Adaptador_Product_Details(productos)
        RCV_Pedido.adapter = adaptador

        Regresar_Detalles.setOnClickListener {
            findNavController().navigate(R.id.navigation_orders_client)
        }

        btn_Eliminar_Pedido.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().CadenaConexion()
                val Eliminar_Venta = objConexion?.prepareStatement("DELETE FROM TbPedido_Cliente WHERE UUID_Pedido = ?")!!

                Eliminar_Venta.setString(1, uuid_Recibido)
                Eliminar_Venta.executeUpdate()

                val commit = objConexion.prepareStatement("COMMIT")
                commit.executeUpdate()
            }
            Toast.makeText(requireContext(), "Se ha eliminado el pedido", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.navigation_orders_client)
        }

        btn_Confirmar_Venta.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().CadenaConexion()

                val Nota = objConexion?.prepareStatement("INSERT INTO TbNotasPedidodos (UUID_Pedido, Nota_Servicio) VALUES (?, ?)")!!
                Nota.setString(1, uuid_Recibido)
                Nota.setString(2, CampoNota.text.toString())
                Nota.executeUpdate()

                val Confirmar_Venta = objConexion?.prepareStatement("UPDATE TbPedido_Cliente SET Pedido_Pendiente = ? WHERE UUID_Pedido = ?")!!
                val Estado_Pedido = "No"
                Confirmar_Venta.setString(1,Estado_Pedido)
                Confirmar_Venta.setString(2, uuid_Recibido)
                Confirmar_Venta.executeUpdate()

            }
            Toast.makeText(requireContext(), "Se ha confirmado el pedido", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.navigation_orders_client)
        }

        return root
    }
}