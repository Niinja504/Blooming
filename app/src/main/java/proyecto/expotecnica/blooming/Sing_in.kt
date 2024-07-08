package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Sing_in : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Variables
        val CampoCorreo = findViewById<EditText>(R.id.txt_Correo_Sing_In)
        val CampoContrasena = findViewById<EditText>(R.id.txt_Contra_Sing_In)
        val OlvidoSuContra = findViewById<TextView>(R.id.lbl_ContraOlvidada_Sing_In)
        val Btn_IniciarSesion = findViewById<Button>(R.id.btn_Iniciar_Sesion_Sing_in)
        val btn_IngresarConGoogle = findViewById<Button>(R.id.btn_Google_Sing_In)
        val Registrarse = findViewById<TextView>(R.id.lbl_Registar_Sing_In)



        //Funciones para abrir las otras pantallas
        OlvidoSuContra.setOnClickListener{
            val PantallaRecuperarContra = Intent(this, Password_recovery1::class.java)
            startActivity(PantallaRecuperarContra)
            finish()
        }

        Registrarse.setOnClickListener{
            val PantallaRegistrarse = Intent(this, Register::class.java)
            startActivity(PantallaRegistrarse)
            finish()
        }

    }
}