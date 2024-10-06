package proyecto.expotecnica.blooming.Client.dedication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Client.delivery_address.DeliveryAddress
import proyecto.expotecnica.blooming.R
import java.sql.Time

class Dedication : Fragment()  {
    private lateinit var CampoDedicatoria: EditText
    private lateinit var CampoNombre: EditText
    private lateinit var SinMensaje: String
    private lateinit var SinNombre: String
    private var UUID_PedidoR: String? = null
    companion object{
        fun newInstance(
            UUID_Pedido: String
        ): DeliveryAddress {
            val fragment = DeliveryAddress ()
            val args = Bundle()
            args.putString("UUID_Pedido", UUID_Pedido)
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
        val root = inflater.inflate(R.layout.fragment_dedication_client, container, false)

        UUID_PedidoR = arguments?.getString("UUID_Pedido")
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_Dedication_client)
        val CbSinMensaje = root.findViewById<MaterialCheckBox>(R.id.cbx_SinMensaje_Tarjeta_Client)
        val CbSinNombre = root.findViewById<MaterialCheckBox >(R.id.cbx_SinNombre_Tarjeta_Client)
        CampoDedicatoria = root.findViewById(R.id.txt_Dedicatoria_Pedido_Client)
        CampoNombre = root.findViewById(R.id.txt_Nombre_Pedido_Client)
        val btn_Dedicatoria = root.findViewById<Button>(R.id.btn_Continuar_Didicatoria_client)

        Regresar.setOnClickListener {
            findNavController().navigate(R.id.delivered_Address)
        }

        CbSinMensaje.setOnCheckedChangeListener { _, isChecked ->
            SinMensaje = if (isChecked) "Si" else "No"
        }

        CbSinNombre.setOnCheckedChangeListener { _, isChecked ->
            SinNombre = if (isChecked) "Si" else "No"
        }

        btn_Dedicatoria.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val Add_Dedicatoria = ObjConexion?.prepareStatement(
                        "INSERT INTO TbDedicatorias (UUID_Pedido, Sin_Mensaje, Dedicatoria, Envio_Sin_Nombre, Nombre_Emisor) VALUES (?, ?, ?, ?, ?)"
                    )!!

                    Add_Dedicatoria.setString(1, UUID_PedidoR)
                    Add_Dedicatoria.setString(2, SinMensaje)
                    Add_Dedicatoria.setString(3, CampoDedicatoria.text.toString())
                    Add_Dedicatoria.setString(4, SinNombre)
                    Add_Dedicatoria.setString(5, CampoNombre.text.toString())
                    Add_Dedicatoria.executeUpdate()
                }
                fragmentManager?.popBackStack()
                fragmentManager?.popBackStack()
                fragmentManager?.popBackStack()
                Toast.makeText(requireContext(), "Se ha finalizado el pedido", Toast.LENGTH_LONG).show()
                LimpiarCamp()
                delay(3000)
                Toast.makeText(requireContext(), "Verifique pedidos pendientes y apruebe el pedido", Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    private fun LimpiarCamp(){
        CampoDedicatoria.text.clear()
        CampoNombre.text.clear()
    }
}