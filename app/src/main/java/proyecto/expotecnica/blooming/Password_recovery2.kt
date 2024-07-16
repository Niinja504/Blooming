package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import modelo.EnvioCorreo

class Password_recovery2 : AppCompatActivity() {
    private lateinit var sentCode: String
    private var resendCount = 0
    private var isTimerRunning = false
    private val maxResends = 10
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password_recovery2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Aquí recibirías el correo electrónico enviado desde Password_recovery1
        val userEmail = intent.getStringExtra("USER_EMAIL")

        val campoCodigo = findViewById<EditText>(R.id.txt_Codigo_Password_Recovery2)
        val botonVerificar = findViewById<Button>(R.id.btn_Verificar_Password_Recovery2)
        val resendTextView = findViewById<TextView>(R.id.lbl_ReenviarCodigo_Password_Recovery2)

        // Generar y enviar el código al iniciar la actividad
        resendCode()

        botonVerificar.setOnClickListener {
            val enteredCode = campoCodigo.text.toString()

            if (enteredCode == sentCode) {
                val intentToRecovery3 = Intent(this, Password_recovery3::class.java)
                intentToRecovery3.putExtra("USER_EMAIL", userEmail)
                startActivity(intentToRecovery3)
                finish()
            } else {
                campoCodigo.error = "El código es incorrecto"
            }
        }

        resendTextView.setOnClickListener {
            if (isTimerRunning) {
                Toast.makeText(this, "No se puede reenviar el código en este momento. Por favor intente más tarde.", Toast.LENGTH_SHORT).show()
            } else if (resendCount < maxResends) {
                resendCount++
                resendCode()
            } else {
                startTimer()
            }
        }
    }

    private fun resendCode() {
        sentCode = generateNewCode()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = intent.getStringExtra("USER_EMAIL") ?: ""
                EnvioCorreo.EnvioDeCorreo(email, "Your Verification Code", "Your verification code is: $sentCode")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Password_recovery2, "Código reenviado a $email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Password_recovery2, "Error al reenviar el correo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        timer = object : CountDownTimer(30 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                isTimerRunning = false
                resendCount = 0
            }
        }.start()
    }

    private fun generateNewCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        var code = ""
        for (i in 0 until 6) {
            code += chars[(Math.random() * chars.length).toInt()]
        }
        return code
    }
}
