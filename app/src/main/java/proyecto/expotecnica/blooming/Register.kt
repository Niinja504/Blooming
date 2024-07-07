package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.*
import modelo.ClaseConexion
import org.checkerframework.common.returnsreceiver.qual.This

class Register : AppCompatActivity() {
    private lateinit var CampoNombres: EditText
    private lateinit var CampoApellidos: EditText
    private lateinit var CampoUsuario: EditText
    private lateinit var CampoTelefono: EditText
    private lateinit var CampoCorreo: EditText
    private lateinit var CampoContra: EditText
    private lateinit var CampoConfirmarContra: EditText
    private lateinit var lbl_IniciarSesion: TextView
    private lateinit var Btn_SubirFoto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Inicializar Firebase
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas
        CampoNombres = findViewById<EditText>(R.id.txt_Nombres_Registrer).apply { filters = arrayOf(InputFilter.LengthFilter(10)) }
        CampoApellidos = findViewById<EditText>(R.id.txt_Apellidos_Registrer).apply { filters = arrayOf(InputFilter.LengthFilter(10)) }
        CampoUsuario = findViewById<EditText>(R.id.txt_Usuario_Registrer).apply { filters = arrayOf(InputFilter.LengthFilter(10)) }
        CampoTelefono = findViewById<EditText>(R.id.txt_Telefono_Registrer).apply {
            filters = arrayOf(InputFilter.LengthFilter(11))
            inputType = InputType.TYPE_CLASS_NUMBER
            addTextChangedListener(TelefonoTextWatcher())
        }

        CampoCorreo = findViewById<EditText>(R.id.txt_Correo_Registrer).apply {
            filters = arrayOf(InputFilter.LengthFilter(20))
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        CampoContra = findViewById<EditText>(R.id.txt_Contrasena_Registrer).apply { filters = arrayOf(InputFilter.LengthFilter(15)) }
        CampoConfirmarContra = findViewById<EditText>(R.id.txt_ConfirmarContra_Registrer).apply { filters = arrayOf(InputFilter.LengthFilter(15)) }
        lbl_IniciarSesion = findViewById<TextView>(R.id.lbl_IniciarSesion_Register)
        Btn_SubirFoto = findViewById<Button>(R.id.btn_foto_perfil_register)

        CampoNombres.requestFocus()

        // Función para abrir la otra pantalla
        lbl_IniciarSesion.setOnClickListener {
            val PantallaIniciarSesion = Intent(this, Sing_in::class.java)
            startActivity(PantallaIniciarSesion)
            finish()
        }

        Btn_SubirFoto.setOnClickListener {
            lifecycleScope.launch {
                if (validarCampos()) {
                    val usuarioExiste = usuarioExiste(CampoUsuario.text.toString())
                    val correoExiste = correoExiste(CampoCorreo.text.toString())

                    if (!usuarioExiste && !correoExiste) {
                        abrirVentanaEmergente()
                    } else {
                        if (usuarioExiste) {
                            CampoUsuario.error = "El usuario ya existe"
                        }
                        if (correoExiste) {
                            CampoCorreo.error = "El correo ya existe"
                        }
                    }
                }
            }
        }
    }

    private suspend fun validarCampos(): Boolean {
        val Nombres = CampoNombres.text.toString()
        val Apellidos = CampoApellidos.text.toString()
        val Usuario = CampoUsuario.text.toString()
        val Telefono = CampoTelefono.text.toString()
        val Correo = CampoCorreo.text.toString()
        val Contra = CampoContra.text.toString()
        val ConfirmarContra = CampoConfirmarContra.text.toString()

        var HayErrores = false

        if (Nombres.isEmpty()) {
            CampoNombres.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombres.error = null
        }

        if (Apellidos.isEmpty()) {
            CampoApellidos.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoApellidos.error = null
        }

        if (Usuario.isEmpty()) {
            CampoUsuario.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoUsuario.error = null
        }

        if (Telefono.isEmpty()) {
            CampoTelefono.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoTelefono.error = null
        }

        if (Correo.isEmpty()) {
            CampoCorreo.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoCorreo.error = null
        }

        if (Contra.isEmpty()) {
            CampoContra.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoContra.error = null
        }

        if (ConfirmarContra.isEmpty()) {
            CampoConfirmarContra.error = "Este campo es obligatorio para verificar su contraseña"
            HayErrores = true
        } else if (ConfirmarContra != Contra) {
            CampoConfirmarContra.error = "Las contraseñas no coinciden"
            HayErrores = true
        } else {
            CampoConfirmarContra.error = null
        }

        if (!Correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.][a-z]+"))) {
            CampoCorreo.error = "El correo no tiene el formato válido"
            HayErrores = true
        } else {
            CampoCorreo.error = null
        }

        if (Contra.length <= 8) {
            CampoContra.error = "La contraseña debe tener más de 8 caracteres"
            HayErrores = true
        } else {
            CampoContra.error = null
        }

        return !HayErrores
    }


    private fun abrirVentanaEmergente() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.imagen_register, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val imgPerfil = dialogView.findViewById<ImageView>(R.id.ImgPerfil_reg)
        val btnSubirImagen = dialogView.findViewById<Button>(R.id.btn_subir_imagen_reg)
        val btnCrearCuenta = dialogView.findViewById<Button>(R.id.btn_CrearCuenta_reg)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btn_cancelar_reg)

        btnSubirImagen.setOnClickListener {
            // Lógica para subir la imagen
            Toast.makeText(this, "Subir imagen", Toast.LENGTH_SHORT).show()
        }

        btnCrearCuenta.setOnClickListener {
            // Lógica para crear la cuenta
            Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            LimpiarCampos()
            dialog.dismiss()
        }

        dialog.show()
    }



    private suspend fun usuarioExiste(usuario: String): Boolean {
        val sql = """
        SELECT COUNT(*) AS usuario_existe
        FROM TbUsers
        WHERE Nombre_de_Usuario = ?
        """.trimIndent()

        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()

        var usuarioExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, usuario)

                val resultado = withContext(Dispatchers.IO) { statement.executeQuery() }

                if (resultado.next()) {
                    val count = resultado.getInt("usuario_existe")
                    usuarioExiste = count > 0
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

        return usuarioExiste
    }

    private suspend fun correoExiste(correo: String): Boolean {
        val sql = """
        SELECT COUNT(*) AS correo_existe
        FROM TbUsers
        WHERE Email_User = ?
        """.trimIndent()

        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()

        var correoExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, correo)

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

    inner class TelefonoTextWatcher : TextWatcher {
        private var isUpdating = false
        private val hyphen = " - "

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return
            isUpdating = true

            var str = s.toString().replace(hyphen, "").replace(" ", "")
            val formatted = StringBuilder()

            for (i in str.indices) {
                formatted.append(str[i])
                if ((i == 3 || i == 7) && i != str.length - 1) {
                    formatted.append(hyphen)
                }
            }

            CampoTelefono.setText(formatted.toString())
            CampoTelefono.setSelection(formatted.length)

            isUpdating = false
        }
    }

    private fun LimpiarCampos() {
        CampoNombres.text.clear()
        CampoApellidos.text.clear()
        CampoUsuario.text.clear()
        CampoTelefono.text.clear()
        CampoCorreo.text.clear()
        CampoContra.text.clear()
        CampoConfirmarContra.text.clear()
    }
}