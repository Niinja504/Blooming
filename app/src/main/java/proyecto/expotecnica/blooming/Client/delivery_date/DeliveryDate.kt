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

        // Habilitar los RadioButtons según la hora actual
        updateRadioButtonState(RadioButton8AM, RadioButton12AM, RadioButton4PM, radioGroup)

        Calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Establecemos la fecha seleccionada =)
            Fechaselec = "$dayOfMonth/${month + 1}/$year"

            // Obtenemos la fecha seleccionada =)
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }

            val currentDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Obtenemos la fecha de mañana =)
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            when {
                selectedDate.after(tomorrow) -> {
                    // Habilitar todos los radio buttons si es un día futuro
                    RadioButton8AM.isEnabled = true
                    RadioButton12AM.isEnabled = true
                    RadioButton4PM.isEnabled = true
                    radioGroup.clearCheck()
                }

                selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                        selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR) -> {
                    updateRadioButtonState(RadioButton8AM, RadioButton12AM, RadioButton4PM, radioGroup)
                }

                else -> {
                    // Deshabilitar todos los botones si es un día pasado
                    RadioButton8AM.isEnabled = false
                    RadioButton12AM.isEnabled = false
                    RadioButton4PM.isEnabled = false
                    radioGroup.clearCheck()
                }
            }
        }

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop_cart_client)
        }

        val brownColor = ContextCompat.getColor(requireContext(), R.color.Brown)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.RD_Hora_8AM_date_client -> {
                    Horario = "8:00 AM - 12:00 PM"
                    ColorRadioButton(RadioButton8AM, brownColor)
                    ColorRadioButton(RadioButton12AM, Color.BLACK)
                    ColorRadioButton(RadioButton4PM, Color.BLACK)
                }
                R.id.RD_Hora_12AM_date_client -> {
                    Horario = "12:00 AM - 4:00 PM"
                    ColorRadioButton(RadioButton8AM, Color.BLACK)
                    ColorRadioButton(RadioButton12AM, brownColor)
                    ColorRadioButton(RadioButton4PM, Color.BLACK)
                }
                R.id.RD_Hora_4AM_date_client -> {
                    Horario = "4:00 AM - 8:00 PM"
                    ColorRadioButton(RadioButton8AM, Color.BLACK)
                    ColorRadioButton(RadioButton12AM, Color.BLACK)
                    ColorRadioButton(RadioButton4PM, brownColor)
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

    private fun updateRadioButtonState(RadioButton8AM: RadioButton, RadioButton12AM: RadioButton, RadioButton4PM: RadioButton, radioGroup: RadioGroup) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            currentHour < 12 -> { // Antes de las 12 PM
                RadioButton8AM.isEnabled = true
                RadioButton12AM.isEnabled = true
                RadioButton4PM.isEnabled = true
            }
            currentHour < 16 -> { // Entre 12 PM y 4 PM
                RadioButton8AM.isEnabled = false
                RadioButton12AM.isEnabled = true
                RadioButton4PM.isEnabled = true
            }
            currentHour < 21 -> { // Entre 4 PM y 9 PM
                RadioButton8AM.isEnabled = false
                RadioButton12AM.isEnabled = false
                RadioButton4PM.isEnabled = true
                radioGroup.check(RadioButton4PM.id)
            }
            else -> { // Después de las 9 PM
                RadioButton8AM.isEnabled = false
                RadioButton12AM.isEnabled = false
                RadioButton4PM.isEnabled = false
                radioGroup.clearCheck()
            }
        }
    }

    private fun ColorRadioButton(radioButton: RadioButton, color: Int) {
        if (radioButton.isEnabled) {
            radioButton.buttonTintList = ColorStateList.valueOf(color)
        } else {
            radioButton.buttonTintList = null
        }
    }
}
