package proyecto.expotecnica.blooming.Client.delivery_date

import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class DeliveryDate : Fragment() {
    private var Fechaselec: String? = null
    private var Horario: String? = null
    companion object{
        fun newInstance(
            UUID_Pedido: String,
            Costo_Venta: Float
        ): DeliveryDate {
            val fragment = DeliveryDate()
            val args = Bundle()
            args.putString("UUID_Pedido", UUID_Pedido)
            args.putFloat("Costo_Pedido", Costo_Venta)
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
        val root = inflater.inflate(R.layout.fragment_delivery_date_client, container, false)

        val UUID_PedidoR = arguments?.getString("UUID_Pedido")
        val Costo_Pedido_Cliente = arguments?.getFloat("Costo_Pedido")
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_Delivery_Date_client)

        // Llamar a todos los elementos
        val Calendario = root.findViewById<CalendarView>(R.id.CVW_Entrega_Pedido_Client)
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = System.currentTimeMillis()
        Calendario.minDate = calendario.timeInMillis
        val radioGroup = root.findViewById<RadioGroup>(R.id.radioGroupHora)
        val RadioButton8AM = root.findViewById<RadioButton>(R.id.RD_Hora_8AM_date_client)
        val RadioButton12AM = root.findViewById<RadioButton>(R.id.RD_Hora_12AM_date_client)
        val RadioButton4PM = root.findViewById<RadioButton>(R.id.RD_Hora_4AM_date_client)
        val BtnDate = root.findViewById<Button>(R.id.btn_ADDFecha_Pedidio_client)

        Calendario.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            Fechaselec = "$dayOfMonth/${month + 1}/$year"
        }

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop_cart_client)
        }

        val brownColor = ContextCompat.getColor(requireContext(), R.color.Brown)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.RD_Hora_8AM_date_client -> {
                    val Hora8 = "8:00 AM - 12:00 PM"
                    Horario = Hora8
                    RadioButton8AM.buttonTintList = ColorStateList.valueOf(brownColor)
                    RadioButton12AM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                    RadioButton4PM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                }

                R.id.RD_Hora_12AM_date_client-> {
                    val Hora12 = "12:00 AM - 4:00 PM"
                    Horario = Hora12
                    RadioButton8AM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                    RadioButton12AM.buttonTintList = ColorStateList.valueOf(brownColor)
                    RadioButton4PM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                }

                R.id.RD_Hora_4AM_date_client -> {
                    val Hora4 = "4:00 AM - 8:00 PM"
                    Horario = Hora4
                    RadioButton8AM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                    RadioButton12AM.buttonTintList = ColorStateList.valueOf(Color.BLACK)
                    RadioButton4PM.buttonTintList = ColorStateList.valueOf(brownColor)
                }
            }
        }

        BtnDate.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val AddDate = ObjConexion?.prepareStatement("INSERT INTO TbHorarioPedido (UUID_Pedido,  Fecha, Horario) VALUES (?, ?, ?)"
                    )!!

                    AddDate.setString(1, UUID_PedidoR)
                    AddDate.setString(2, Fechaselec)
                    AddDate.setString(3, Horario)
                    AddDate.executeUpdate()
                }
                val bundle = Bundle().apply {
                    putString("UUID_Pedido", UUID_PedidoR)
                    putFloat("Costo_Pedido", Costo_Pedido_Cliente ?: 0f)
                }
                findNavController().navigate(R.id.delivered_Address, bundle)
            }
        }
        return root
    }
}
