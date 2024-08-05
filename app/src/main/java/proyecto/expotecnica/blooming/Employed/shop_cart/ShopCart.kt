package proyecto.expotecnica.blooming.Employed.shop_cart

import RecyclerViewHelpers.Adaptador_ShopCart_Employed
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Employed.SharedViewModel_Product
import proyecto.expotecnica.blooming.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ShopCart : Fragment() {
    private lateinit var CampoNombre: EditText
    private lateinit var lbl_Total: TextView
    private val sharedViewModel: SharedViewModel_Product by activityViewModels()
    private lateinit var adapter: Adaptador_ShopCart_Employed
    private var Hora: String? = null
    private var Fecha: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_shop_cart_employed, container, false)

        val RCV_Inventory = root.findViewById<RecyclerView>(R.id.RCV_ShopCart_Employed)
        RCV_Inventory.layoutManager = LinearLayoutManager(requireContext())

        adapter = Adaptador_ShopCart_Employed(emptyList(), sharedViewModel, this::ActualizaTotalVenta)
        RCV_Inventory.adapter = adapter

        sharedViewModel.productList.observe(viewLifecycleOwner) { productList ->
            adapter.Datos = productList
            adapter.notifyDataSetChanged()
            ActualizaTotalVenta()
        }

        CampoNombre = root.findViewById(R.id.txt_NombreCliente_Venta_Employed)
        lbl_Total = root.findViewById(R.id.lbl_TotalVenta_ShopCart_Employed)

        val BtnAdd_Venta = root.findViewById<Button>(R.id.btn_CrearVenta_Employed)

        CampoNombre.filters = arrayOf(InputFilter.LengthFilter(14))

        ObtenerFechaYHora()
        ActualizaTotalVenta()

        BtnAdd_Venta.setOnClickListener {
            lifecycleScope.launch {
                if (ValidarCampo()) {
                    val totalVenta = calcularTotalVenta()
                    val UUID_Ven = UUID.randomUUID().toString()
                    withContext(Dispatchers.IO) {
                        val ObjConexion = ClaseConexion().CadenaConexion()
                        val AddVenta = ObjConexion?.prepareStatement(
                            "INSERT INTO TbVentaEncaja (UUID_Venta, UUID_Empleado, Fecha_Venta, Hora_Venta, Nombre_Cliente, Total_Venta) VALUES (?, ?, ?, ?, ?, ?)"
                        )!!
                        AddVenta.setString(1, UUID_Ven)
                        AddVenta.setString(2, "156a438c-201f-4065-9903-65d7fc44a65f")
                        AddVenta.setString(3, Fecha)
                        AddVenta.setString(4, Hora)
                        AddVenta.setString(5, CampoNombre.text.toString())
                        AddVenta.setFloat(6, totalVenta)
                        AddVenta.executeUpdate()

                        val AddProducto = ObjConexion?.prepareStatement(
                            "INSERT INTO TbVentaArticulos (UUID_Venta, UUID_Producto, Precio_Producto, Cantidad_Producto) VALUES (?, ?, ?, ?)"
                        )!!
                        sharedViewModel.productList.value?.forEach { producto ->
                            AddProducto.setString(1, UUID_Ven)
                            AddProducto.setString(2, producto.uuid)
                            AddProducto.setFloat(3, producto.precio)
                            AddProducto.setInt(4, producto.cantidad)
                            AddProducto.executeUpdate()
                        }
                    }
                    LimpiarCampos()
                }
            }
        }

        return root
    }

    private fun ValidarCampo(): Boolean {
        val Nombre = CampoNombre.text.toString()
        var HayErrores = false

        if (Nombre.isEmpty()) {
            CampoNombre.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombre.error = null
        }

        return !HayErrores
    }

    private fun ObtenerFechaYHora() {
        val currentDate = Date()

        val horaFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        Hora = horaFormat.format(currentDate)

        val fechaFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Fecha = fechaFormat.format(currentDate)
    }

    private fun calcularTotalVenta(): Float {
        val products = sharedViewModel.productList.value ?: emptyList()
        return products
            .map { it.precio * it.cantidad }
            .sum()
    }

    private fun ActualizaTotalVenta() {
        val totalVenta = calcularTotalVenta()
        lbl_Total.text = totalVenta.toString()
    }

    private fun LimpiarCampos(){
        CampoNombre.text.clear()
    }
}
