package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.EnvioCorreo
import java.security.MessageDigest

class Sing_in : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Variables
        val CampoCorreo = findViewById<EditText>(R.id.txt_Correo_Sing_In)
        val CampoContrasena = findViewById<EditText>(R.id.txt_Contra_Sing_In)
        val OlvidoSuContra = findViewById<TextView>(R.id.lbl_ContraOlvidada_Sing_In)
        val Btn_IniciarSesion = findViewById<Button>(R.id.btn_Iniciar_Sesion_Sing_in)
        val btn_IngresarConGoogle = findViewById<Button>(R.id.btn_Google_Sing_In)
        val Registrarse = findViewById<TextView>(R.id.lbl_Registar_Sing_In)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar botón para iniciar sesión con Google
        btn_IngresarConGoogle.setOnClickListener {
            signInWithGoogle()
        }

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun EnviarCorreo(text: String) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceDetails = getDeviceDetails()
                    val message = """
                        Se ha iniciado sesión en un nuevo dispositivo.
                        Nombre del dispositivo: ${deviceDetails.deviceName}
                        Modelo: ${deviceDetails.model}
                        Marca: ${deviceDetails.manufacturer}
                    """.trimIndent()
                    EnvioCorreo.EnvioDeCorreo(text, "Nuevo Inicio de Sesión", message)
                } catch (e: Exception) {
                    // Manejar el error de envío de correo aquí
                    e.printStackTrace()
                }
            }
        }

        // Inicio de sesion con correo y contraseña
        Btn_IniciarSesion.setOnClickListener {
            // Preparo el intent para cambiar a la pantalla de bienvenida
            val pantallaPrincipal = Intent(this, Dashboard_client::class.java)
            // Dentro de una corrutina hago un select en la base de datos
            GlobalScope.launch(Dispatchers.IO) {
                // Creo un objeto de la clase conexión
                val objConexion = ClaseConexion().CadenaConexion()

                // Encripto la contraseña usando la función hashSHA256
                val contraseniaEncriptada = hashSHA256(CampoContrasena.text.toString())

                val comprobarUsuario = objConexion?.prepareStatement("SELECT * FROM TbUsers WHERE Email_User = ? AND Contra_User = ?")!!
                comprobarUsuario.setString(1, CampoCorreo.text.toString())
                comprobarUsuario.setString(2, contraseniaEncriptada)
                val resultado = comprobarUsuario.executeQuery()
                // Si encuentra un resultado
                if (resultado.next()) {
                    startActivity(pantallaPrincipal)
                    EnviarCorreo(CampoCorreo.text.toString())
                    finish()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Sing_in, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Funciones para abrir las otras pantallas
        OlvidoSuContra.setOnClickListener {
            val PantallaRecuperarContra = Intent(this, Password_recovery1::class.java)
            startActivity(PantallaRecuperarContra)
            finish()
        }

        Registrarse.setOnClickListener {
            val PantallaRegistrarse = Intent(this, Register::class.java)
            startActivity(PantallaRegistrarse)
            finish()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado del intento de inicio de sesión de Google
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    showUsernameDialog(account)
                } else {
                    // Manejar el caso donde account es null
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                // Manejar el error
                e.printStackTrace()
                Toast.makeText(this, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUsernameDialog(account: GoogleSignInAccount) {
        val dialogView = layoutInflater.inflate(R.layout.nombre_usuario_fireb, null)
        val editTextUsername = dialogView.findViewById<EditText>(R.id.txt_Usuario_fireb)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btn_sig_Usuario_Fireb)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ingrese su nombre de usuario")
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnSubmit.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                alertDialog.dismiss()
                showPhoneNumberDialog(account, username)
            } else {
                editTextUsername.error = "Ingrese un nombre de usuario válido"
            }
        }

        alertDialog.show()
    }

    private fun showPhoneNumberDialog(account: GoogleSignInAccount, username: String) {
        val dialogView = layoutInflater.inflate(R.layout.telefono_usuario_fireb, null)
        val editTextPhoneNumber = dialogView.findViewById<EditText>(R.id.txt_Telef_usu_Fireb)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btn_Sig_Telefono_Fireb)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ingrese su número de teléfono")
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnSubmit.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                alertDialog.dismiss()
                showProfileDialog(account, username, phoneNumber)
            } else {
                editTextPhoneNumber.error = "Ingrese un número de teléfono válido"
            }
        }

        alertDialog.show()
    }

    private fun showProfileDialog(account: GoogleSignInAccount, username: String, phoneNumber: String) {
        val dialogView = layoutInflater.inflate(R.layout.img_singin_firebase, null)
        val btnUploadImage = dialogView.findViewById<Button>(R.id.btn_subir_imagen_reg)
        val btnCreateAccount = dialogView.findViewById<Button>(R.id.btn_CrearCuenta_reg)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancelar_reg)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Perfil de usuario")
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnCreateAccount.setOnClickListener {
            // Aquí puedes realizar la inserción de los datos en Firebase
            // Por ejemplo: firebaseAuth.createUserWithEmailAndPassword(...)
            Toast.makeText(this@Sing_in, "Datos almacenados: Usuario: $username, Teléfono: $phoneNumber", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        val user = firebaseAuth.currentUser
                        // Token
                        val token = account.idToken
                        // Aquí podrías mostrar el primer diálogo para capturar el nombre de usuario
                        showUsernameDialog(account)
                    } else {
                        // Fallo en el inicio de sesión
                        Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Manejar el caso donde account es null
            Toast.makeText(this, "Cuenta de Google nula", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para obtener los detalles del dispositivo
    data class DeviceDetails(val deviceName: String, val manufacturer: String, val model: String)

    private fun getDeviceDetails(): DeviceDetails {
        val manufacturer = Build.MANUFACTURER.capitalize()
        val model = Build.MODEL
        val deviceName = if (model.startsWith(manufacturer)) {
            model.capitalize()
        } else {
            "$manufacturer $model"
        }
        return DeviceDetails(deviceName, manufacturer, model)
    }
}
