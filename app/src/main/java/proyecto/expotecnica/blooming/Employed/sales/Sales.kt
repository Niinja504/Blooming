package proyecto.expotecnica.blooming.Employed.sales

import DataC.Data_Sales
import RecyclerViewHelpers.Adaptador_Sales_Admin
import RecyclerViewHelpers.Adaptador_Sales_Employed
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Employed.ImageViewModel_Employed
import proyecto.expotecnica.blooming.R
import java.sql.Connection

class Sales : Fragment() {
    private val imageViewModel: ImageViewModel_Employed by activityViewModels()
    private var miAdaptador: Adaptador_Sales_Employed? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_sales_employed, container, false)

        val IC_Regresar = root.findViewById<ImageView>(R.id.Regresar_Sales_Employed)
        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_Sales_Employed)
        val RCV_Sales = root.findViewById<RecyclerView>(R.id.RCV_Sales_Employed)
        //Asignarle un Layout al RecyclerView
        RCV_Sales.layoutManager = LinearLayoutManager(requireContext())

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        IC_Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_orders_employed)
        }

        fun ObtenerNombreEmpleado(uuidEmpleado: String, objConexion: Connection): String {
            val preparedStatement = objConexion.prepareStatement("SELECT Nombres_User FROM TbUsers WHERE UUID_User = ?")
            preparedStatement.setString(1, uuidEmpleado)
            val resultSet = preparedStatement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getString("Nombres_User") ?: ""
            } else {
                ""
            }
        }

        suspend fun MostrarDatos(): List<Data_Sales> {
            val objConexion = ClaseConexion().CadenaConexion() ?: return emptyList()
            val preparedStatement = objConexion.prepareStatement("""
            SELECT 
            e.UUID_Venta AS uuid,
            e.UUID_Empleado AS uuid_Empleado,
            e.Fecha_Venta AS FechaVenta,
            e.Hora_Venta AS HoraVenta,
            e.Nombre_Cliente AS NombreCliente,
            e.Total_Venta AS TotalVenta,
            a.UUID_Producto AS UUID_Producto,
            a.Precio_Producto AS PrecioProducto,
            a.Cantidad_Producto AS CantidadProducto
           FROM TbVentaEncaja e
           LEFT JOIN TbVentaArticulos a ON e.UUID_Venta = a.UUID_Venta
           ORDER BY e.Fecha_Venta DESC, e.Hora_Venta DESC
           """)

            val resultSet = preparedStatement.executeQuery() ?: return emptyList()

            val ventas = mutableListOf<Data_Sales>()
            while (resultSet.next()) {
                val uuidEmpleado = resultSet.getString("uuid_Empleado") ?: ""
                val nombreEmpleado = ObtenerNombreEmpleado(uuidEmpleado, objConexion)

                val venta = Data_Sales(
                    uuid = resultSet.getString("uuid") ?: "",
                    uuid_Empleado = uuidEmpleado,
                    FechaVenta = resultSet.getString("FechaVenta") ?: "",
                    HoraVenta = resultSet.getString("HoraVenta") ?: "",
                    NombreCliente = resultSet.getString("NombreCliente") ?: "",
                    NombreEmpleado = nombreEmpleado,
                    TotalVenta = resultSet.getFloat("TotalVenta"),
                    UUID_Producto = resultSet.getString("UUID_Producto") ?: "",
                    PrecioProducto = resultSet.getFloat("PrecioProducto"),
                    CantidadProducto = resultSet.getInt("CantidadProducto")
                )
                ventas.add(venta)
            }
            return ventas
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val VentasBD = MostrarDatos()
            withContext(Dispatchers.Main){
                miAdaptador = Adaptador_Sales_Employed(VentasBD)
                RCV_Sales.adapter = miAdaptador
            }
        }
        return root
    }
}