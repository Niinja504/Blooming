package proyecto.expotecnica.blooming

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import modelo.ClaseConexion
import modelo.EnvioCorreo
import modelo.ImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck


class Sing_in : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        // Configurar Firebase App Check
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

        // Variables
        val campoCorreo: EditText = findViewById(R.id.txt_Correo_Sing_In)
        val campoContrasena: EditText = findViewById(R.id.txt_Contra_Sing_In)
        val olvidoSuContra: TextView = findViewById(R.id.lbl_ContraOlvidada_Sing_In)
        val btnIniciarSesion: Button = findViewById(R.id.btn_Iniciar_Sesion_Sing_in)
        val btnIngresarConGoogle: Button = findViewById(R.id.btn_Google_Sing_In)
        val registrarse: TextView = findViewById(R.id.lbl_Registar_Sing_In)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar botón para iniciar sesión con Google
        btnIngresarConGoogle.setOnClickListener {
            signInWithGoogle()
        }

        fun hashSHA256(input: String): String {
            val bytes: ByteArray = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun enviarCorreo(text: String) {
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
                    e.printStackTrace()
                }
            }
        }

        // Inicio de sesión con correo y contraseña
        btnIniciarSesion.setOnClickListener {
            val pantallaPrincipal = Intent(this, Dashboard_client::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion: Connection? = ClaseConexion().CadenaConexion()
                val contraseniaEncriptada: String = hashSHA256(campoContrasena.text.toString())
                val comprobarUsuario: PreparedStatement = objConexion?.prepareStatement("SELECT * FROM TbUsers WHERE Email_User = ? AND Contra_User = ?")!!
                comprobarUsuario.setString(1, campoCorreo.text.toString())
                comprobarUsuario.setString(2, contraseniaEncriptada)
                val resultado: ResultSet = comprobarUsuario.executeQuery()
                if (resultado.next()) {
                    startActivity(pantallaPrincipal)
                    enviarCorreo(campoCorreo.text.toString())
                    finish()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Sing_in, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Funciones para abrir las otras pantallas
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

    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d("GoogleSignIn", "Cuenta obtenida: ${account.email}")
                    handleGoogleSignInResult(account)
                } else {
                    Log.e("GoogleSignIn", "Cuenta es null")
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "ApiException: ${e.statusCode}, ${e.message}")
                Toast.makeText(this, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == Activity.RESULT_OK) {
            val imgPerfil: ImageView? = dialogView.findViewById(R.id.ImgPerfil_Fib)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_Fib -> {
                    currentPhotoPath?.let {
                        val fileUri: Uri = Uri.fromFile(File(it))
                        selectedImageUri = fileUri
                        imgPerfil?.let {
                            Glide.with(this)
                                .load(fileUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(it)
                        }
                    }
                }
                REQUEST_IMAGE_PICK_Fib -> {
                    val fileUri: Uri? = data?.data
                    selectedImageUri = fileUri
                    imgPerfil?.let {
                        Glide.with(this)
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(it)
                    }
                }
            }
        }
    }


    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            val token: String = account.idToken ?: ""
            if (tokExiste(token)) {
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@Sing_in, Dashboard_client::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    showUsernameDialog(account)
                }
            }
        }
    }

    private suspend fun tokExiste(token: String): Boolean {
        val sql = "SELECT COUNT(*) AS token_existe FROM TbUsers_Firebase WHERE IdToken_User_Firebase = ?"
        val claseConexion = ClaseConexion()
        val conexion: Connection? = claseConexion.CadenaConexion()

        var tokenExiste = false

        if (conexion != null) {
            try {
                val statement: PreparedStatement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, token)

                val resultado: ResultSet = withContext(Dispatchers.IO) { statement.executeQuery() }

                if (resultado.next()) {
                    val count: Int = resultado.getInt("token_existe")
                    tokenExiste = count > 0
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

        return tokenExiste
    }

    private fun showUsernameDialog(account: GoogleSignInAccount) {
        dialogView = layoutInflater.inflate(R.layout.nombre_usuario_fireb, null)
        val editTextUsername: EditText = dialogView.findViewById(R.id.txt_Usuario_fireb)
        val btnSubmit: Button = dialogView.findViewById(R.id.btn_sig_Usuario_Fireb)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ingrese su nombre de usuario")
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnSubmit.setOnClickListener {
            val username: String = editTextUsername.text.toString().trim()
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
        dialogView = layoutInflater.inflate(R.layout.telefono_usuario_fireb, null)
        val editTextPhoneNumber: EditText = dialogView.findViewById(R.id.txt_Telef_usu_Fireb)
        val btnSubmit: Button = dialogView.findViewById(R.id.btn_Sig_Telefono_Fireb)

        editTextPhoneNumber.inputType = InputType.TYPE_CLASS_NUMBER
        editTextPhoneNumber.addTextChangedListener(TelefonoTextWatcher(editTextPhoneNumber))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ingrese su número de teléfono")
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnSubmit.setOnClickListener {
            val phoneNumber: String = editTextPhoneNumber.text.toString().replace(" - ", "").trim()
            if (phoneNumber.length < 8) {
                editTextPhoneNumber.error = "El número telefonico debe tener 8 digitos"
            } else {
                alertDialog.dismiss()
                showProfilePictureDialog(account, username, phoneNumber)
            }
        }

        alertDialog.show()
    }


    private fun showProfilePictureDialog(account: GoogleSignInAccount, username: String, phoneNumber: String) {
        dialogView = layoutInflater.inflate(R.layout.img_singin_firebase, null)
        val btn_SubirFoto: Button = dialogView.findViewById(R.id.btn_subir_img_Fib)
        val btn_CrarCuenta: Button = dialogView.findViewById(R.id.btn_CrearCuenta_Fib)
        val btnCancelar: Button = dialogView.findViewById(R.id.btn_cancelar_Fib)
        val imgPerfil: ImageView = dialogView.findViewById(R.id.ImgPerfil_Fib)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btn_SubirFoto.setOnClickListener{
            mostrarDialogoSeleccionImagen()
        }

        btn_CrarCuenta.setOnClickListener {
            lifecycleScope.launch {
                val imageUrl = if (selectedImageUri != null) {
                    val imageBitmap = getBitmapFromUri(selectedImageUri!!)
                    val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                    uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                } else {
                    "El usuario eligio la imagen predeternimada"
                }

                // Inserción en la base de datos
                withContext(Dispatchers.IO) {
                    val ObjConexion = ClaseConexion().CadenaConexion()

                    val CrearCuentaFireb = ObjConexion?.prepareStatement(
                        "INSERT INTO TbUsers_Firebase (ID_User_Firebase, UUID_User_Firebase, Token_Google_Firebase, Nombres_User_Firebase, Num_Telefono_Firebase, Img_User_Firebase) VALUES (SEQ_Firebase.NEXTVAL, ?, ?, ?, ?, ?)"
                    )!!

                    CrearCuentaFireb.setString(1, UUID.randomUUID().toString())
                    CrearCuentaFireb.setString(2, account.idToken)
                    CrearCuentaFireb.setString(3, username)
                    CrearCuentaFireb.setString(4, phoneNumber)
                    CrearCuentaFireb.setString(5, imageUrl)
                    CrearCuentaFireb.executeUpdate()
                }

                alertDialog.dismiss()
                registerUserInFirebase(account, username, phoneNumber, imageUrl)
            }
        }

        btnCancelar.setOnClickListener{
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun mostrarDialogoSeleccionImagen() {
        val opciones = arrayOf("Cámara", "Galería")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> abrirGaleria()
                }
            }
            .show()
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            ImageUtils.createImageFile(this)
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            currentPhotoPath = it.absolutePath
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_Fib)
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK_Fib)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    private suspend fun uploadImageToFirebase(bitmap: Bitmap, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().reference.child("Clientes_Firebase/$fileName.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
    }

    data class UserInfo(val username: String, val phoneNumber: String, val profilePictureUri: Uri?)

    private fun registerUserInFirebase(account: GoogleSignInAccount, username: String, phoneNumber: String, imageUrl: String?) {
        val user = hashMapOf(
            "username" to username,
            "phoneNumber" to phoneNumber,
            "profilePictureUrl" to imageUrl,
            "email" to account.email
        )

        val db = Firebase.firestore
        val uid = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "DocumentSnapshot successfully written!")
                val intent = Intent(this, Dashboard_client::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error writing document", e)
                Toast.makeText(this, "Error al registrar el usuario en Firebase", Toast.LENGTH_SHORT).show()
            }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, muestra el cuadro de diálogo para el nombre de usuario
                    showUsernameDialog(account)
                } else {
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE_Fib = 1
        private const val REQUEST_IMAGE_PICK_Fib = 2
    }

    inner class TelefonoTextWatcher(private val editTextPhoneNumber: EditText) : TextWatcher {
        private var isUpdating = false
        private val hyphen = " - "
        private val maxLength = 8

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return
            isUpdating = true

            // Filtrar solo números
            var str = s.toString().replace(Regex("[^0-9]"), "")
            if (str.length > maxLength) {
                str = str.substring(0, maxLength)
            }
            val formatted = StringBuilder()

            for (i in str.indices) {
                formatted.append(str[i])
                if ((i == 3 || i == 7) && i != str.length - 1) {
                    formatted.append(hyphen)
                }
            }

            editTextPhoneNumber.setText(formatted.toString())
            editTextPhoneNumber.setSelection(formatted.length)

            isUpdating = false
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