package proyecto.expotecnica.blooming

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.EnvioCorreo
import proyecto.expotecnica.blooming.Sing_in.DeviceDetails
import java.security.MessageDigest
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Password_recovery3 : AppCompatActivity() {
    private lateinit var CampoNuevaContra: EditText
    private lateinit var CampoConfirmarContra: EditText
    private lateinit var ImgOjoNuevaContra: ImageView
    private lateinit var ImgOjoConfirmarContra: ImageView
    private lateinit var Btn_CambiarContra: Button
    private var isNuevaContraVisible = false
    private var isConfirmarContraVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password_recovery3)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CampoNuevaContra = findViewById(R.id.txt_NuevaContra_Password_Recovery3)
        CampoConfirmarContra = findViewById(R.id.txt_ConfirmacionContra_Password_Recovery3)
        ImgOjoNuevaContra = findViewById(R.id.Img_NuevaContra_Password_Recovery3)
        ImgOjoConfirmarContra = findViewById(R.id.Img_ConfirmarContra_Password_Recovery3)
        Btn_CambiarContra = findViewById(R.id.btn_CambiarContra_Password_Recovery3)

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
                CampoConfirmarContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ImgOjoConfirmarContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoConfirmarContra.inputType = InputType.TYPE_CLASS_TEXT
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

            //En este bloque de código se verifica que el correo no sea nulo y también actualiza el password =)
            if (userEmail != null) {
                GlobalScope.launch {
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val ContraEncrip = hashSHA256(nuevaContra)

                    val Actualizar = ObjConexion?.prepareStatement("UPDATE TbUsers SET Contra_User = ? WHERE Email_User = ?")!!
                    Actualizar.setString(1, ContraEncrip)
                    Actualizar.setString(2, userEmail)
                    Actualizar.executeUpdate()

                    val COMMIT = ObjConexion.prepareStatement("COMMIT")
                    COMMIT.executeUpdate()
                }
                LimpiarCampos()
                Toast.makeText(this, "Se ha cambiado correctamente la contraseña", Toast.LENGTH_LONG).show()
                EnviarCorreo(userEmail)
            } else {
                Log.e("Error", "El correo electrónico no está disponible")
            }
        }
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

    //Metodo en encriptado que es un hash =)
    private fun hashSHA256(input: String): String {
        val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
