package proyecto.expotecnica.blooming

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Corrutine
        GlobalScope.launch(Dispatchers.Main) {
            //Espera 5.5 segundos antes de abrir la activity por medio de un delay
            delay(5400)
            //Inicia  la activity de sing in
            startActivity(Intent(this@SplashScreen, Sing_in::class.java))
            //Con esto nos aseguramos que se cierre la activity y no se pueda volver a atras
            finish()
        }
    }
}