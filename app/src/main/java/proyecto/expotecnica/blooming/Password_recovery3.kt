package proyecto.expotecnica.blooming

import android.os.Bundle
import android.text.InputType
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
import java.security.MessageDigest
import java.sql.SQLException
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
                CampoNuevaContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ImgOjoNuevaContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoNuevaContra.inputType = InputType.TYPE_CLASS_TEXT
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

            if (userEmail != null) {  // Asegurarse de que userEmail no sea nulo
                CoroutineScope(Dispatchers.Main).launch {
                    if (actualizarContrasena(userEmail, nuevaContra)) {
                        Toast.makeText(this@Password_recovery3, "Contraseña actualizada exitosamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@Password_recovery3, "Usuario no encontrado", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this@Password_recovery3, "Correo electrónico no encontrado", Toast.LENGTH_LONG).show()
            }
        }
    }

    suspend fun buscarUUIDEnTbUsers(userEmail: String): String? {
        val conexion = ClaseConexion().CadenaConexion()
        return conexion?.use { conn ->
            val query = "SELECT UUID_User FROM TbUsers WHERE Email_User = ?"
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, userEmail)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                rs.getString("UUID_User")
            } else {
                null
            }
        }
    }

    suspend fun buscarUUIDEnTbUsersEmployedAdmin(userEmail: String): String? {
        val conexion = ClaseConexion().CadenaConexion()
        return conexion?.use { conn ->
            val query = "SELECT UUID_Employed_Admin FROM TbUSers_Employed_Admin WHERE Correo_Employed_Admin = ?"
            val stmt = conn.prepareStatement(query)
            stmt.setString(1, userEmail)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                rs.getString("UUID_Employed_Admin")
            } else {
                null
            }
        }
    }

    suspend fun actualizarContrasena(userEmail: String, nuevaContrasena: String): Boolean {
        val uuidTbUsers = buscarUUIDEnTbUsers(userEmail)

        val conexion = ClaseConexion().CadenaConexion()
        return conexion?.use { conn ->
            if (uuidTbUsers != null) {
                val updateQuery = "UPDATE TbUsers SET Contra_User = ? WHERE Email_User = ?"
                val stmt = conn.prepareStatement(updateQuery)
                stmt.setString(1, nuevaContrasena)
                stmt.setString(2, userEmail)
                stmt.executeUpdate() > 0
            } else {
                val uuidEmployedAdmin = buscarUUIDEnTbUsersEmployedAdmin(userEmail)
                if (uuidEmployedAdmin != null) {
                    val updateQuery = "UPDATE TbUSers_Employed_Admin SET Contra_Employed_Admin = ? WHERE Correo_Employed_Admin = ?"
                    val stmt = conn.prepareStatement(updateQuery)
                    stmt.setString(1, nuevaContrasena)
                    stmt.setString(2, userEmail)
                    stmt.executeUpdate() > 0
                } else {
                    false
                }
            }
        } ?: false
    }

    private fun hashSHA256(input: String): String {
        val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
