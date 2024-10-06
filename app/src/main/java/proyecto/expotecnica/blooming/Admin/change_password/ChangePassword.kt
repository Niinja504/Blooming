package proyecto.expotecnica.blooming.Admin.change_password

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.EnvioCorreo
import proyecto.expotecnica.blooming.R
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.log

class ChangePassword : Fragment() {
    private lateinit var CampoNuevaContra: EditText
    private lateinit var CampoConfirmarContra: EditText
    private lateinit var ImgOjoNuevaContra: ImageView
    private lateinit var ImgOjoConfirmarContra: ImageView
    private lateinit var Btn_CambiarContra: Button
    private var isNuevaContraVisible = false
    private var isConfirmarContraVisible = false
    private var UUID: String? = null
    private var Correo: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            UUID = it.getString("UUID")
            Correo = it.getString("Correo")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_change_password_admin, container, false)

        val Regresar_ChangePassword = root.findViewById<ImageView>(R.id.IC_Regresar_ChangePassword_Admin)
        CampoNuevaContra = root.findViewById(R.id.txt_NuevaContra_ChangePassword_Admin)
        CampoConfirmarContra = root.findViewById(R.id.txt_ConfirmarContra_ChangePassword_Admin)
        ImgOjoNuevaContra = root.findViewById(R.id.Img_NuevaContra_ChangePassword_Admin)
        ImgOjoConfirmarContra = root.findViewById(R.id.Img_ConfirmarContra_ChangePassword_Admin)
        Btn_CambiarContra = root.findViewById(R.id.btn_GuardarContra_ChangePassword_Admin)

        CampoNuevaContra.requestFocus()

        CampoNuevaContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        CampoConfirmarContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        ImgOjoNuevaContra.setOnClickListener {
            if (isNuevaContraVisible) {
                CampoNuevaContra.transformationMethod = PasswordTransformationMethod.getInstance()
                ImgOjoNuevaContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoNuevaContra.transformationMethod = null
                ImgOjoNuevaContra.setImageResource(R.drawable.ic_show_password)
            }
            isNuevaContraVisible = !isNuevaContraVisible
            CampoNuevaContra.setSelection(CampoNuevaContra.text.length)
        }

        ImgOjoConfirmarContra.setOnClickListener {
            if (isConfirmarContraVisible) {
                CampoConfirmarContra.transformationMethod = PasswordTransformationMethod.getInstance()
                ImgOjoConfirmarContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoConfirmarContra.transformationMethod = null
                ImgOjoConfirmarContra.setImageResource(R.drawable.ic_show_password)
            }
            isConfirmarContraVisible = !isConfirmarContraVisible
            CampoConfirmarContra.setSelection(CampoConfirmarContra.text.length)
        }

        Btn_CambiarContra.setOnClickListener {
            val nuevaContra = CampoNuevaContra.text.toString()
            if (nuevaContra.isEmpty()) {
                CampoNuevaContra.error = "Este campo no puede estar vacío"
                return@setOnClickListener
            }

            val confirmarContra = CampoConfirmarContra.text.toString()
            if (nuevaContra != confirmarContra) {
                CampoConfirmarContra.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            GlobalScope.launch {
                val ObjConexion = ClaseConexion().CadenaConexion()
                val ContraEncrip = hashSHA256(nuevaContra)

                val Actualizar = ObjConexion?.prepareStatement("UPDATE TbUsers SET Contra_User = ? WHERE UUID_User = ?")!!
                Actualizar.setString(1, ContraEncrip)
                Actualizar.setString(2, UUID)
                Actualizar.executeUpdate()

                val COMMIT = ObjConexion.prepareStatement("COMMIT")
                COMMIT.executeUpdate()
            }
            LimpiarCampos()
            Toast.makeText(requireContext(), "Su contraseña se ha actualizado", Toast.LENGTH_LONG).show()
            EnviarCorreo(Correo.toString())
        }

        Regresar_ChangePassword.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile_admin)
        }


        return root
    }

    private fun EnviarCorreo(destinatario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceDetails = getDeviceDetails()
                val message = """
                <html>
                <body>
                    <p>Se ha cambiado su contraseña</p>
                    <p><strong>Fecha:</strong> ${deviceDetails.date}</p>
                    <p><strong>Hora:</strong> ${deviceDetails.time}</p>
                    <p><strong>Nombre del dispositivo:</strong> ${deviceDetails.deviceName}</p>
                    <p><strong>Modelo:</strong> ${deviceDetails.model}</p>
                    <p><strong>Marca:</strong> ${deviceDetails.manufacturer}</p>
                    <br>
                    <p>Saludos cordiales,</p>
                    <footer style="text-align: center; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px;">
                        <strong>Soporte de Blooming</strong>
                        <p>Ubicación: San Salvador, El Salvador</p>
                        <p>Correo: <a href="mailto:correo@empresa.com">correo@empresa.com</a></p>
                        <p>Síguenos en nuestras redes sociales:</p>
                        <p>
                            <a href="https://facebook.com/empresa" target="_blank">
                                <img src="https://example.com/icons/facebook.png" alt="Facebook" width="24" height="24"/>
                            </a>
                            <a href="https://twitter.com/empresa" target="_blank">
                                <img src="https://example.com/icons/twitter.png" alt="Twitter" width="24" height="24"/>
                            </a>
                            <a href="https://instagram.com/empresa" target="_blank">
                                <img src="https://example.com/icons/instagram.png" alt="Instagram" width="24" height="24"/>
                            </a>
                        </p>
                    </footer>
                </body>
                </html>
            """.trimIndent()

                EnvioCorreo.EnvioDeCorreo(destinatario, "Nuevo Inicio de Sesión", message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    data class DeviceDetails(val deviceName: String, val manufacturer: String, val model: String, val date: String, val time: String)

    private fun getDeviceDetails(): DeviceDetails {
        val manufacturer = Build.MANUFACTURER.capitalize()
        val model = Build.MODEL
        val deviceName = if (model.startsWith(manufacturer)) {
            model.capitalize()
        } else {
            "$manufacturer $model"
        }

        val now = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val date = now.format(dateFormatter)
        val time = now.format(timeFormatter)

        return DeviceDetails(deviceName, manufacturer, model, date, time)
    }

    private fun LimpiarCampos(){
        CampoNuevaContra.text.clear()
        CampoConfirmarContra.text.clear()
    }

    private fun hashSHA256(input: String): String {
        val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}