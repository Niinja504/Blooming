package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import modelo.ClaseConexion
import modelo.EnvioCorreo
import java.security.MessageDigest
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Sing_in : AppCompatActivity() {
    //Variable a nivel de clase =)
    private lateinit var campoCorreo: EditText
    private lateinit var campoContrasena: EditText
    private lateinit var ImgOjo: ImageView
    private var isContraVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Variables que se estaran utilizando que previamente se inicializaron a nivel de clase =)
        campoCorreo = findViewById(R.id.txt_Correo_Sing_In)
        campoContrasena = findViewById(R.id.txt_Contra_Sing_In)
        ImgOjo = findViewById(R.id.Img_SingIn)
        val olvidoSuContra: TextView = findViewById(R.id.lbl_ContraOlvidada_Sing_In)
        val btnIniciarSesion: Button = findViewById(R.id.btn_Iniciar_Sesion_Sing_in)
        val registrarse: TextView = findViewById(R.id.lbl_Registar_Sing_In)

        //Condiciòn que nos permite cambiar de icono por medio de un control de click =)
        ImgOjo.setOnClickListener {
            if (isContraVisible) {
                campoContrasena.transformationMethod = PasswordTransformationMethod.getInstance()
                ImgOjo.setImageResource(R.drawable.ic_hide_password)
            } else {
                campoContrasena.transformationMethod = null
                ImgOjo.setImageResource(R.drawable.ic_show_password)
            }
            isContraVisible = !isContraVisible
            campoContrasena.setSelection(campoContrasena.text.length)
        }

        fun hashSHA256(input: String): String {
            val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun enviarCorreo(destinatario: String) {
                  CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceDetails = getDeviceDetails()
                    val message = """
                <html>
                <body>
                    <p>Se ha iniciado sesión en un nuevo dispositivo.</p>
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
                        <p>Correo: <a href="mailto:bloomingservicee@gmail.com">bloomingservicee@gmail.com</a></p>
                        <p>Síguenos en nuestras redes sociales:</p>
                        <p>
                            <a href="https://www.instagram.com/_sistema_blooming?igsh=aWRtOWZ4cHZsMnli" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/15713/15713420.png" alt="Facebook" width="24" height="24"/>
                            </a>
                            <a href="https://x.com/SistemaBlooming" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/5968/5968830.png" alt="Twitter" width="24" height="24"/>
                            </a>
                            <a href="https://www.tiktok.com/@sistema_blooming?_t=8oRwbbrEw6g&_r=1" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/15713/15713399.png" alt="Instagram" width="24" height="24"/>
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

        // Inicio de sesión con correo y contraseña =)
        btnIniciarSesion.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    if (withContext(Dispatchers.Main) { ValidarCampos() }){
                        val objConexion: Connection? = ClaseConexion().CadenaConexion()
                        val contrasenaEncriptada: String = hashSHA256(campoContrasena.text.toString())
                        val email = campoCorreo.text.toString()

                        if (!correoExiste(email)) {
                            withContext(Dispatchers.Main) {
                                campoCorreo.error = "El correo no existe"
                            }
                            return@launch
                        }

                        var rol: Int? = null
                        var usuarioEncontrado = false
                        var uuid: String? = null

                        if (!usuarioEncontrado) {
                            val ComprobarUsuario: PreparedStatement = objConexion?.prepareStatement("SELECT * FROM TbUsers WHERE Email_User = ? AND Contra_User = ?")!!
                            ComprobarUsuario.setString(1, email)
                            ComprobarUsuario.setString(2, contrasenaEncriptada)
                            val Resultado: ResultSet = ComprobarUsuario.executeQuery()

                            // Verificar el rol en la  tabla =)
                            if (Resultado.next()) {
                                usuarioEncontrado = true
                                val rolString = Resultado.getString("Rol_User")
                                rol = when (rolString) {
                                    "Administrador" -> 0
                                    "Empleado" -> 1
                                    "Cliente" -> 2
                                    else -> null
                                }
                                uuid = Resultado.getString("UUID_User")
                            }
                        }

                        // Redireccionar según el rol encontrado =)
                        withContext(Dispatchers.Main) {
                            if (usuarioEncontrado) {
                                when (rol) {
                                    0 -> {
                                        //Tira una coroutina en el hilo io para actualizar la sesion del usuario =)
                                        withContext(Dispatchers.IO){
                                            val ObjConexion = ClaseConexion().CadenaConexion()
                                            val Abrir = ObjConexion?.prepareStatement("UPDATE TbUsers SET Sesion_User = ? WHERE UUID_User = ?")!!
                                            val Abierto = 1

                                            Abrir.setInt(1, Abierto)
                                            Abrir.setString(2, uuid)
                                            Abrir.executeUpdate()
                                        }
                                        val pantallaAdmin = Intent(this@Sing_in, Dashboard_admin::class.java)
                                        pantallaAdmin.putExtra("UUID", uuid)
                                        pantallaAdmin.putExtra("Correo", email)
                                        startActivity(pantallaAdmin) // Administrador
                                    }

                                    1 -> {
                                        withContext(Dispatchers.IO){
                                            val ObjConexion = ClaseConexion().CadenaConexion()
                                            val Abrir = ObjConexion?.prepareStatement("UPDATE TbUsers SET Sesion_User = ? WHERE UUID_User = ?")!!
                                            val Abierto = 1

                                            Abrir.setInt(1, Abierto)
                                            Abrir.setString(2, uuid)
                                            Abrir.executeUpdate()
                                        }
                                        val pantallaEmpleado = Intent(this@Sing_in, Dashboard_employed::class.java)
                                        pantallaEmpleado.putExtra("UUID", uuid)
                                        pantallaEmpleado.putExtra("Correo", email)
                                        startActivity(pantallaEmpleado) // Empleado
                                    }

                                    2 -> {
                                        withContext(Dispatchers.IO){
                                            val ObjConexion = ClaseConexion().CadenaConexion()
                                            val Abrir = ObjConexion?.prepareStatement("UPDATE TbUsers SET Sesion_User = ? WHERE UUID_User = ?")!!
                                            val Abierto = 1

                                            Abrir.setInt(1, Abierto)
                                            Abrir.setString(2, uuid)
                                            Abrir.executeUpdate()
                                        }
                                        val pantallaPrincipal = Intent(this@Sing_in, Dashboard_client::class.java)
                                        pantallaPrincipal.putExtra("UUID", uuid)
                                        pantallaPrincipal.putExtra("Correo", email)
                                        startActivity(pantallaPrincipal) // Cliente
                                    }
                                    else -> Toast.makeText(this@Sing_in, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                }
                                enviarCorreo(email)
                                finish()
                            } else {
                                Toast.makeText(this@Sing_in, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Sing_in, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // eventos para abrir las otras pantallas
        olvidoSuContra.setOnClickListener {
            val pantallaRecuperarContra = Intent(this, Password_recovery1::class.java)
            startActivity(pantallaRecuperarContra)
            finish()
        }

        registrarse.setOnClickListener {
            val pantallaRegistrarse = Intent(this, Register::class.java)
            startActivity(pantallaRegistrarse)
            finish()
        }
    }

    //Metodo que nos permitira validar todos los campos para que no sean vacios
    private fun ValidarCampos(): Boolean {
        val Correo = campoCorreo.text.toString()
        val Contra = campoContrasena.text.toString()

        var HayErrores = false

        if (Correo.isEmpty()) {
            campoCorreo.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            campoCorreo.error = null
        }

        if (Contra.isEmpty()) {
            campoContrasena.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            campoContrasena.error = null
        }

        return !HayErrores
    }

    private suspend fun correoExiste(correo: String): Boolean {
        val sql = "SELECT COUNT(*) AS correo_existe FROM TbUsers WHERE Email_User = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()

        var correoExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, correo) // Pasamos el parámetro de manera segura

                val resultado = withContext(Dispatchers.IO) { statement.executeQuery() }

                if (resultado.next()) {
                    val count = resultado.getInt("correo_existe")
                    correoExiste = count > 0
                }
            } catch (e: Exception) {
                println("Error al ejecutar la consulta SQL: $e")
            } finally {
                try {
                    withContext(Dispatchers.IO) { conexion.close() }
                } catch (e: Exception) {
                    println("Error al cerrar la conexión: $e")
                }
            }
        } else {
            println("No se pudo establecer la conexión a la base de datos.")
        }

        return correoExiste
    }

    // Método para obtener los detalles del dispositivo y luego pasarle los parametros al correo que se enviara
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
}