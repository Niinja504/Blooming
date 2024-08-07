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

            GlobalScope.launch {
                val ObjConexion = ClaseConexion().CadenaConexion()

                val ContraEncrip = hashSHA256(CampoNuevaContra.text.toString())

                val Actualizar = ObjConexion?.prepareStatement("UPDATE TbUsers SET Contra_User = ? WHERE Email_User = ?")!!
                Actualizar.setString(1, ContraEncrip)
                Actualizar.setString(2, userEmail)
                Actualizar.executeUpdate()

                val COMMIT = ObjConexion.prepareStatement("COMMIT")
                COMMIT.executeUpdate()

                println("se actualizo exitosamente")
            }

        }
    }

    private fun hashSHA256(input: String): String {
        val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
