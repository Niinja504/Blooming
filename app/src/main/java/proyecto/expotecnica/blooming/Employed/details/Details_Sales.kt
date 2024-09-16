package proyecto.expotecnica.blooming.Employed.details

import DataC.Data_Productos
import RecyclerViewHelpers.Adaptador_Product_Details
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.Admin.details.Details_Sales
import proyecto.expotecnica.blooming.R

class Details_Sales : Fragment() {
    private lateinit var productos: List<Data_Productos>
    companion object{
        fun newInstance(
            uuid: String,
            uuid_Empleado: String,
            FechaVenta: String,
            HoraVenta: String,
            NombreCliente: String,
            NombreEmpleado: String,
            TotalVenta: Float,
            UUID_Producto: String,
            PrecioProducto: Float,
            CantidadProducto: Int
        ): Details_Sales {
            val fragment = Details_Sales()
            val args = Bundle()
            args.putString("uuid", uuid)
            args.putString("uuid_Empleado", uuid_Empleado)
            args.putString("FechaVenta", FechaVenta)
            args.putString("HoraVenta", HoraVenta)
            args.putString("NombreCliente", NombreCliente)
            args.putString("NombreEmpleado", NombreEmpleado)
            args.putFloat("TotalVenta", TotalVenta)
            args.putString("UUID_Producto", UUID_Producto)
            args.putFloat("PrecioProducto", PrecioProducto)
            args.putInt("CantidadProducto", CantidadProducto)
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
        val root = inflater.inflate(R.layout.fragment_details_sales_employed, container, false)

        val Regresar_Detalles = root.findViewById<ImageView>(R.id.IC_Regresar_Sale_Employed)

        val RCV_Pedido = root.findViewById<RecyclerView>(R.id.RCV_Detalles_Sale_Employed)
        RCV_Pedido.layoutManager = LinearLayoutManager(requireContext())

        arguments?.let {
            productos = it.getParcelableArrayList("productos") ?: emptyList()
        }

        val uuid_Recibido = arguments?.getString("uuid")
        val Hora_Venta_Recibida = arguments?.getString("HoraVenta")
        val Fecha_Venta_Recibida = arguments?.getString("FechaVenta")
        val Nombre_Cliente_Recibido = arguments?.getString("NombreCliente")
        val Nombre_Empleado_Recibido = arguments?.getString("NombreEmpleado")
        val Total_Venta_Recibido = arguments?.getFloat("TotalVenta")
        val UUID_Producto_Recibido = arguments?.getString("UUID_Producto")
        val Precio_Producto_Recibido = arguments?.getString("PrecioProducto")
        val Cantidad_Producto_Recibido = arguments?.getString("CantidadProducto")

        val lbl_Hora_Venta = root.findViewById<TextView>(R.id.lbl_HoraVenta_Details_Orders_Employed)
        val lbl_Fecha_Venta = root.findViewById<TextView>(R.id.lbl_FechaVenta_Details_Orders_Employed)
        val lbl_Nombre_Cliente_Venta = root.findViewById<TextView>(R.id.lbl_NombreCliente_Details_Orders_Employed)
        val lbl_Nombre_Empleado_Venta = root.findViewById<TextView>(R.id.lbl_NombreEmpleado_Details_Orders_Employed)
        val lbl_Total_Venta = root.findViewById<TextView>(R.id.lbl_TotalVenta_Details_Orders_Employed)

        lbl_Hora_Venta.text = Hora_Venta_Recibida
        lbl_Fecha_Venta.text = Fecha_Venta_Recibida
        lbl_Nombre_Cliente_Venta.text = Nombre_Cliente_Recibido
        lbl_Nombre_Empleado_Venta.text = Nombre_Empleado_Recibido
        lbl_Total_Venta.text = Total_Venta_Recibido.toString()

        val adaptador = Adaptador_Product_Details(productos)
        RCV_Pedido.adapter = adaptador

        Regresar_Detalles.setOnClickListener {
            findNavController().navigate(R.id.navigation_sales_employed)
        }

        return root
    }
}