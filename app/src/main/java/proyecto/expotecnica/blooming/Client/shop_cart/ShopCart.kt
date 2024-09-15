package proyecto.expotecnica.blooming.Client.shop_cart

import RecyclerViewHelpers.Adaptador_ShopCart_Client
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Client.ImageViewModel_Client
import proyecto.expotecnica.blooming.Client.SharedViewModel_Product_Client
import proyecto.expotecnica.blooming.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ShopCart : Fragment() {
    private lateinit var lbl_Total: TextView
    private lateinit var lbl_Descuento: TextView
    private val imageViewModel: ImageViewModel_Client by activityViewModels()
    private val sharedViewModel: SharedViewModel_Product_Client by activityViewModels()
    private lateinit var adapter: Adaptador_ShopCart_Client
    private var Hora: String? = null
    private var Fecha: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_shop_cart_client, container, false)

        val RCV_Inventory = root.findViewById<RecyclerView>(R.id.RCV_ShopCart_Client)
        RCV_Inventory.layoutManager = LinearLayoutManager(requireContext())
        val IMGUser = root.findViewById<ImageView>(R.id.IMG_User_ShopCart_Client)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMGUser.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMGUser)
            }
        }

        adapter = Adaptador_ShopCart_Client(emptyList(), sharedViewModel, this::ActualizaTotalVenta)
        RCV_Inventory.adapter = adapter

        sharedViewModel.productList.observe(viewLifecycleOwner) { productList ->
            adapter.Datos = productList
            adapter.notifyDataSetChanged()
            ObtenerDescuentos()
            ActualizaTotalVenta()
        }

        lbl_Total = root.findViewById(R.id.lbl_TotalVenta_ShopCart_Client)
        lbl_Descuento = root.findViewById(R.id.lbl_Porcentaje_ShopCart_Client)

        val BtnAdd_Pedido = root.findViewById<Button>(R.id.btn_Continuar_ShopCart_client)

        ObtenerFechaYHora()
        ActualizaTotalVenta()

        BtnAdd_Pedido.setOnClickListener {
            lifecycleScope.launch {
                val totalVenta = calcularTotalVenta().toFloat()
                val UUID_Pedido = UUID.randomUUID().toString()
                val Pendiente = "Si"

                withContext(Dispatchers.IO) {
                    val ObjConexion = ClaseConexion().CadenaConexion()

                    try {
                        val AddPedido = ObjConexion?.prepareStatement(
                            "INSERT INTO TbPedido_Cliente (UUID_Pedido, UUID_Cliente, Fecha_Venta, Hora_Venta, Subtotal, Pedido_Pendiente) VALUES (?, ?, ?, ?, ?, ?)"
                        )!!
                        AddPedido.setString(1, UUID_Pedido)
                        AddPedido.setString(2, imageViewModel.uuid.value)
                        AddPedido.setString(3, Fecha)
                        AddPedido.setString(4, Hora)
                        AddPedido.setBigDecimal(5, BigDecimal(totalVenta.toDouble()).setScale(2, RoundingMode.HALF_EVEN))
                        AddPedido.setString(6, Pendiente)
                        AddPedido.executeUpdate()

                        val AddProducto = ObjConexion.prepareStatement(
                            "INSERT INTO TbProductosPedido (UUID_Pedido, UUID_Producto, Precio_Producto, Cantidad_Producto) VALUES (?, ?, ?, ?)"
                        )
                        sharedViewModel.productList.value?.forEach { producto ->
                            val descuento = sharedViewModel.descuentos.value?.get(producto.uuid) ?: 0
                            val precioOriginal = BigDecimal(producto.precio.toDouble())
                            val porcentajeDescuento = BigDecimal(descuento).divide(BigDecimal(100))
                            val precioConDescuento = if (descuento > 0) {
                                precioOriginal.multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
                            } else {
                                precioOriginal
                            }

                            AddProducto.setString(1, UUID_Pedido)
                            AddProducto.setString(2, producto.uuid)
                            AddProducto.setBigDecimal(3, precioConDescuento.setScale(2, RoundingMode.HALF_EVEN))
                            AddProducto.setInt(4, producto.cantidad)
                            AddProducto.executeUpdate()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        ObjConexion?.close()
                    }
                }

                val bundle = Bundle().apply {
                    val Costo_Venta = BigDecimal(totalVenta.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                    putString("UUID_Pedido", UUID_Pedido)
                    putFloat("Costo_Pedido", Costo_Venta.toFloat())
                }
                sharedViewModel.LimpiarListaProductos()
                findNavController().navigate(R.id.delivered_date, bundle)
            }
        }

        return root
    }

    private fun ObtenerDescuentos() {
        CoroutineScope(Dispatchers.IO).launch {
            val ObjConexion = ClaseConexion().CadenaConexion()
            val descuentos = mutableMapOf<String, Int>()

            try {
                val query = "SELECT UUID_Producto, Porcentaje_Oferta FROM TbOfertas WHERE UUID_Producto IN (${sharedViewModel.productList.value?.joinToString(",") { "?" }})"

                val statement = ObjConexion?.prepareStatement(query)

                sharedViewModel.productList.value?.forEachIndexed { index, producto ->
                    statement?.setString(index + 1, producto.uuid)
                }

                val resultSet = statement?.executeQuery()
                while (resultSet?.next() == true) {
                    val uuidProducto = resultSet.getString("UUID_Producto")
                    val porcentajeOfertaStr = resultSet.getString("Porcentaje_Oferta")
                    val porcentajeOferta = porcentajeOfertaStr.replace("%", "").toIntOrNull() ?: 0
                    descuentos[uuidProducto] = porcentajeOferta
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                ObjConexion?.close()
            }

            withContext(Dispatchers.Main) {
                sharedViewModel.setDescuentos(descuentos)
                ActualizaTotalVenta()
            }
        }
    }

    private fun ObtenerFechaYHora() {
        val currentDate = Date()

        val horaFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        Hora = horaFormat.format(currentDate)

        val fechaFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Fecha = fechaFormat.format(currentDate)
    }

    private fun calcularTotalVenta(): BigDecimal {
        val products = sharedViewModel.productList.value ?: emptyList()
        val descuentos = sharedViewModel.descuentos.value ?: emptyMap()
        val mensajeDescuentos = StringBuilder()

        val totalVenta = products
            .map { producto ->
                val precioOriginal = BigDecimal.valueOf(producto.precio.toDouble())
                val cantidad = BigDecimal(producto.cantidad)
                val descuento = descuentos[producto.uuid] ?: 0
                val porcentajeDescuento = BigDecimal(descuento).divide(BigDecimal(100))
                val precioConDescuento = if (descuento > 0) {
                    precioOriginal.multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
                } else {
                    precioOriginal
                }

                if (descuento > 0) {
                    mensajeDescuentos.append("Se le aplicó un descuento del ${descuento}% a ${producto.nombre}.\n")
                }

                precioConDescuento.multiply(cantidad)
            }
            .reduceOrNull { acc, value -> acc.add(value) } ?: BigDecimal.ZERO

        if (mensajeDescuentos.isNotEmpty()) {
            Toast.makeText(requireContext(), mensajeDescuentos.toString(), Toast.LENGTH_LONG).show()
        }

        return totalVenta
    }

    private fun calcularPorcentajeTotalDescuento(): BigDecimal {
        val products = sharedViewModel.productList.value ?: emptyList()
        val descuentos = sharedViewModel.descuentos.value ?: emptyMap()

        val totalOriginal = products
            .map { producto ->
                BigDecimal.valueOf(producto.precio.toDouble()).multiply(BigDecimal(producto.cantidad))
            }
            .reduceOrNull { acc, value -> acc.add(value) } ?: BigDecimal.ZERO

        val totalConDescuento = products
            .map { producto ->
                val precioOriginal = BigDecimal.valueOf(producto.precio.toDouble())
                val cantidad = BigDecimal(producto.cantidad)
                val descuento = descuentos[producto.uuid] ?: 0
                val porcentajeDescuento = BigDecimal(descuento).divide(BigDecimal(100))
                val precioConDescuento = if (descuento > 0) {
                    precioOriginal.multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
                } else {
                    precioOriginal
                }
                precioConDescuento.multiply(cantidad)
            }
            .reduceOrNull { acc, value -> acc.add(value) } ?: BigDecimal.ZERO

        return if (totalOriginal.compareTo(BigDecimal.ZERO) > 0) {
            val descuentoTotal = totalOriginal.subtract(totalConDescuento)
            descuentoTotal.divide(totalOriginal, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal(100))
                .setScale(2, RoundingMode.HALF_EVEN)
        } else {
            BigDecimal.ZERO
        }
    }

    private fun ActualizaTotalVenta() {
        val totalVenta = calcularTotalVenta()
        val porcentajeDescuentoTotal = calcularPorcentajeTotalDescuento()
        lbl_Total.text = totalVenta.setScale(2, RoundingMode.HALF_EVEN).toString()
        lbl_Descuento.text = "${porcentajeDescuentoTotal.toPlainString()}%"
    }
}