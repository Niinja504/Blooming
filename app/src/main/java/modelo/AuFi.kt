package modelo
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AuFi(private val context: Context) {
    private var imageUrl: String? = null

    fun setImageUrl(url: String) {
        imageUrl = url
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Elimina la duplicación

    fun authenticateUser(onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign-in successful
                onSuccess()
            } else {
                // If sign-in fails, display a message to the user.
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                onFailure()
            }
        }
    }
}