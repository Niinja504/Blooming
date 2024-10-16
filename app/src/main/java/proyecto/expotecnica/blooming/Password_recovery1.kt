package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.EnvioCorreo
import java.security.SecureRandom

class Password_recovery1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password_recovery1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Variables que se va a utilizar
        val CampoCorreo = findViewById<EditText>(R.id.txt_Correo_Password_Recovery1)
        val BotonEnviar = findViewById<Button>(R.id.btnIniciarSesion)
        val RecuerdaSuContra = findViewById<TextView>(R.id.lbl_RecuerdaContra_Password_Recovery1)

        BotonEnviar.setOnClickListener {
            val text = CampoCorreo.text.toString()
            //Con este bucle de tipo when verificamos que no sea vacio ninguno de los campos =/
            when {
                text.isEmpty() -> {
                    CampoCorreo.error = "El campo de texto está vacío"
                }

                !text.contains("@") -> {
                    CampoCorreo.error = "El campo debe de contener un @"
                }

                else -> {
                    val code = generateRandomCode()
                    CoroutineScope(Dispatchers.IO).launch {
                        val correo = CampoCorreo.text.toString()
                        val correoExiste = correoExisteBD(correo)

                        if (correoExiste) {
                            try {
                                EnvioCorreo.EnvioDeCorreo(correo, "Your Verification Code", "Your verification code is: $code")

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@Password_recovery1, "Código enviado a $correo", Toast.LENGTH_SHORT).show()

                                    // Intent para Password_recovery2 osea para pasarle los argumentos a la otra pantalla
                                    val intentToRecovery2 = Intent(this@Password_recovery1, Password_recovery2::class.java)
                                    intentToRecovery2.putExtra("SENT_CODE", code)
                                    intentToRecovery2.putExtra("USER_EMAIL", correo)
                                    startActivity(intentToRecovery2)

                                }

                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@Password_recovery1, "Error al enviar el correo: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                CampoCorreo.error = "El correo no existe"
                            }
                        }
                    }
                }
            }
        }

        //Navegamos a la otra pantalla si el usuario asi lo desea
        RecuerdaSuContra.setOnClickListener {
            val intent = Intent(this,Sing_in::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Con este metodo nos aseguramos que el correo este asociado a una cuenta existente antes de enviar el correo =)
    private suspend fun correoExisteBD(correo: String): Boolean {
        val sql_BD1 = "SELECT COUNT(*) AS correo_existe FROM TbUsers WHERE Email_User = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()
        var correoExiste = false

        if (conexion != null) {
            try {
                // Verificamos en la primera tabla °_°
                val statement1 = withContext(Dispatchers.IO) { conexion.prepareStatement(sql_BD1) }
                statement1.setString(1, correo)

                val resultado1 = withContext(Dispatchers.IO) { statement1.executeQuery() }
                if (resultado1.next()) {
                    val count1 = resultado1.getInt("correo_existe")
                    correoExiste = count1 > 0
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

    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz123456789"
        return (1..6)
            .map { chars[SecureRandom().nextInt(chars.length)] }
            .joinToString("")
    }
}
