package proyecto.expotecnica.blooming.Employed.details

import DataC.Data_Productos
import RecyclerViewHelpers.Adaptador_Product_Details
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val root = inflater.inflate(R.layout.fragment_details_orders_employed, container, false)

        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_Orders_Employed)

        val RCV_Pedido = root.findViewById<RecyclerView>(R.id.RCV_Detalles_Pedido_Employed)
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

        val lbl_Nombre_Cliente = root.findViewById<TextView>(R.id.lbl_NombreCliente_Details_Orders_Employed)
        val lbl_Fecha_Entrega = root.findViewById<TextView>(R.id.lbl_Fecha_Entrega_Details_orders_Employed)
        val lbl_Direccion_Entrega = root.findViewById<TextView>(R.id.lbl_Direccion_Details_Orders_Employed)
        val lbl_Dedicatoria = root.findViewById<TextView>(R.id.lbl_Dedicatoria_Orders_Employed)
        val lbl_SubTotal = root.findViewById<TextView>(R.id.lbl_SubTotal_Details_Orders_Employed)
        val lbl_Costo_Envio = root.findViewById<TextView>(R.id.lbl_Envio_Details_Orders_Employed)
        val lbl_Nota_Pedido = root.findViewById<TextView>(R.id.lbl_Nota_Details_Orders_Employed)

        lbl_Nombre_Cliente.text = Nombre_Cliente_Recibido
        lbl_Direccion_Entrega.text = Lugar_Entrega_Recibido
        lbl_SubTotal.text = SubTotal_Recibido.toString()
        lbl_Dedicatoria.text = Sin_Mensaje_Recibido

        val adaptador = Adaptador_Product_Details(productos)
        RCV_Pedido.adapter = adaptador

        Regresar_Detalles.setOnClickListener {
            findNavController().navigate(R.id.navigation_orders_employed)
        }

        return root
    }
}