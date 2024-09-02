package proyecto.expotecnica.blooming.Client.shop_cart

import RecyclerViewHelpers.Adaptador_ShopCart_Client
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
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
            ActualizaTotalVenta()
        }

        lbl_Total = root.findViewById(R.id.lbl_TotalVenta_ShopCart_Client)

        val BtnAdd_Pedido = root.findViewById<Button>(R.id.btn_Continuar_ShopCart_client)

        ObtenerFechaYHora()
        ActualizaTotalVenta()

        BtnAdd_Pedido.setOnClickListener {
            lifecycleScope.launch{
                val totalVenta = calcularTotalVenta().toFloat()
                val UUID_Pedido = UUID.randomUUID().toString()
                withContext(Dispatchers.IO) {
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val AddPedido = ObjConexion?.prepareStatement("INSERT INTO TbPedido_Cliente (UUID_Pedido, UUID_Cliente, Fecha_Venta, Hora_Venta, Subtotal) VALUES (?, ?, ?, ?, ?)"
                    )!!
                    AddPedido.setString(1, UUID_Pedido)
                    AddPedido.setString(2, "36ac0650-22b0-4fc8-a356-71adc7102773")
                    AddPedido.setString(3, Fecha)
                    AddPedido.setString(4, Hora)
                    AddPedido.setFloat(5, totalVenta)
                    AddPedido.executeUpdate()

                    val AddProducto = ObjConexion?.prepareStatement(
                        "INSERT INTO TbProductosPedido (UUID_Pedido, UUID_Producto, Precio_Producto, Cantidad_Producto) VALUES (?, ?, ?, ?)"
                    )!!
                    sharedViewModel.productList.value?.forEach{ producto ->
                        AddProducto.setString(1, UUID_Pedido)
                        AddProducto.setString(2, producto.uuid)
                        AddProducto.setFloat(3, producto.precio)
                        AddProducto.setInt(4, producto.cantidad)
                        AddProducto.executeUpdate()
                    }
                }
                val bundle = Bundle().apply {
                    putString("UUID_Pedido", UUID_Pedido)
                }
                sharedViewModel.LimpiarListaProductos()
                findNavController().navigate(R.id.delivered_date, bundle)
            }
        }

        return root
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
        return if (products.isEmpty()) {
            BigDecimal.ZERO
        } else {
            products
                .map { producto ->
                    val precio = BigDecimal.valueOf(producto.precio.toDouble())
                    val cantidad = BigDecimal(producto.cantidad)
                    precio.multiply(cantidad)
                }
                .reduce { acc, value -> acc.add(value) }
        }
    }

    private fun ActualizaTotalVenta() {
        val totalVenta = calcularTotalVenta()
        lbl_Total.text = totalVenta.setScale(2, RoundingMode.HALF_EVEN).toString()
    }
}