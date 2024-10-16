package proyecto.expotecnica.blooming

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import modelo.EnvioCorreo
import proyecto.expotecnica.blooming.Sing_in.DeviceDetails
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Register : AppCompatActivity() {
    private lateinit var CampoNombres: EditText
    private lateinit var CampoApellidos: EditText
    private lateinit var CampoUsuario: EditText
    private lateinit var CampoTelefono: EditText
    private lateinit var CampoEdad: EditText
    private lateinit var CampoCorreo: EditText
    private lateinit var CampoContra: EditText
    private lateinit var CampoConfirmarContra: EditText
    private lateinit var lbl_IniciarSesion: TextView
    private lateinit var Btn_SubirFoto: Button
    private lateinit var dialogView: View
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var ImgOjoNewContra: ImageView
    private lateinit var ImgOjoConfirContra: ImageView
    private var isNewContraVisible = false
    private var isConfirContraVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas y componentes
        CampoNombres = findViewById(R.id.txt_Nombres_Registrer)
        CampoApellidos = findViewById(R.id.txt_Apellidos_Registrer)
        CampoUsuario = findViewById(R.id.txt_Usuario_Registrer)
        CampoTelefono = findViewById(R.id.txt_Telefono_Registrer)
        CampoEdad = findViewById(R.id.txt_Edad_Registrer)
        CampoCorreo = findViewById(R.id.txt_Correo_Registrer)
        ImgOjoNewContra = findViewById(R.id.Img_Contra_Register)
        ImgOjoConfirContra = findViewById(R.id.Img_ConfirmarContra_Register)
        CampoContra = findViewById(R.id.txt_Contrasena_Registrer)
        CampoConfirmarContra = findViewById(R.id.txt_ConfirmarContra_Registrer)
        lbl_IniciarSesion = findViewById(R.id.lbl_IniciarSesion_Register)
        Btn_SubirFoto = findViewById(R.id.btn_foto_perfil_register)

        ImgOjoNewContra.setOnClickListener {
            if (isNewContraVisible) {
                CampoContra.transformationMethod = PasswordTransformationMethod.getInstance()
                ImgOjoNewContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoContra.transformationMethod = null
                ImgOjoNewContra.setImageResource(R.drawable.ic_show_password)
            }
            isNewContraVisible = !isNewContraVisible
            CampoContra.setSelection(CampoContra.text.length)
        }

        ImgOjoConfirContra.setOnClickListener {
            if (isConfirContraVisible) {
                CampoConfirmarContra.transformationMethod = PasswordTransformationMethod.getInstance()
                ImgOjoConfirContra.setImageResource(R.drawable.ic_hide_password)
            } else {
                CampoConfirmarContra.transformationMethod = null
                ImgOjoConfirContra.setImageResource(R.drawable.ic_show_password)
            }
            isConfirContraVisible = !isConfirContraVisible
            CampoConfirmarContra.setSelection(CampoConfirmarContra.text.length)
        }

        CampoNombres.filters = arrayOf(InputFilter.LengthFilter(15))
        CampoApellidos.filters = arrayOf(InputFilter.LengthFilter(15))
        CampoUsuario.filters = arrayOf(InputFilter.LengthFilter(10))
        CampoTelefono.filters = arrayOf(InputFilter.LengthFilter(11))
        CampoTelefono.inputType = InputType.TYPE_CLASS_NUMBER
        CampoTelefono.addTextChangedListener(TelefonoTextWatcher())

        CampoEdad.filters = arrayOf(InputFilter.LengthFilter(2))
        CampoCorreo.filters = arrayOf(InputFilter.LengthFilter(30))
        CampoCorreo.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        CampoContra.filters = arrayOf(InputFilter.LengthFilter(20))
        CampoConfirmarContra.filters = arrayOf(InputFilter.LengthFilter(20))

        CampoNombres.requestFocus()

        //Evento para abrir la otra pantalla =\
        lbl_IniciarSesion.setOnClickListener {
            AbrirVenSingIn()
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

    private fun AbrirVenSingIn(){
        val PantallaIniciarSesion = Intent(this, Sing_in::class.java)
        startActivity(PantallaIniciarSesion)
        finish()
    }

    private suspend fun validarCampos(): Boolean {
        val Nombres = CampoNombres.text.toString()
        val Apellidos = CampoApellidos.text.toString()
        val Usuario = CampoUsuario.text.toString()
        val Telefono = CampoTelefono.text.toString()
        val Edad = CampoEdad.text.toString()
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

        if (Edad.isEmpty()) {
            CampoEdad.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoEdad.error = null
        }

        if (Edad < 18.toString()) {
            CampoEdad.error = "Debes ser mayor de edad"
            HayErrores = true
        } else {
            CampoEdad.error = null
        }

        if (Telefono.length < 8) {
            CampoTelefono.error = "El número telefonico debe de tener 8 digitos"
            HayErrores = true
        } else {
            CampoContra.error = null
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

    private suspend fun usuarioExiste(usuario: String): Boolean {
        val sql = "SELECT COUNT(*) AS usuario_existe FROM TbUsers WHERE Nombre_de_Usuario = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()

        var usuarioExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, usuario) // Pasamos el parámetro de manera segura

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
        val sql = "SELECT COUNT(*) AS correo_existe FROM TbUsers WHERE Email_User = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()

        var correoExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, correo) // Pasamos el parámetro de manera segura

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

    private fun abrirVentanaEmergente() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.imagen_register, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        this.dialogView = dialogView // Guardar la referencia del dialogView

        val imgPerfil = dialogView.findViewById<ImageView>(R.id.ImgPerfil_reg)
        val btnSubirImagen = dialogView.findViewById<Button>(R.id.btn_subir_imagen_reg)
        val btnCrearCuenta = dialogView.findViewById<Button>(R.id.btn_CrearCuenta_reg)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btn_cancelar_reg)

        btnSubirImagen.setOnClickListener {
            mostrarDialogoSeleccionImagen()
        }

        btnCrearCuenta.setOnClickListener {
            lifecycleScope.launch {
                val email = CampoCorreo.text.toString()
                val imageUrl = if (selectedImageUri != null) {
                    val imageBitmap = getBitmapFromUri(selectedImageUri!!)
                    val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                    uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                } else {
                    "El usuario eligio la imagen predeternimada"
                }

                Toast.makeText(applicationContext, "Por favor, no cierre la aplicación, ya que se está creando la cuenta. Gracias", Toast.LENGTH_SHORT).show()

                // Inserción en la base de datos
                withContext(Dispatchers.IO) {
                    val ObjConexion = ClaseConexion().CadenaConexion()

                    // Encripto la contraseña
                    val ContraEncrip = hashSHA256(CampoContra.text.toString())
                    val Sesion = 0
                    val Rol = "Cliente"

                    val Crear = ObjConexion?.prepareStatement(
                        "INSERT INTO TbUsers (UUID_User, Nombres_User, Apellido_User, Nombre_de_Usuario, Num_Telefono_User, Edad_User, Email_User, Contra_User, Img_User, Rol_User, Sesion_User) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    )!!

                    val UUID_User = UUID.randomUUID().toString()

                    Crear.setString(1, UUID_User)
                    Crear.setString(2, CampoNombres.text.toString())
                    Crear.setString(3, CampoApellidos.text.toString())
                    Crear.setString(4, CampoUsuario.text.toString())
                    Crear.setString(5, CampoTelefono.text.toString())
                    Crear.setInt(6, CampoEdad.text.toString().toInt())
                    Crear.setString(7, email)
                    Crear.setString(8, ContraEncrip)
                    Crear.setString(9, imageUrl)
                    Crear.setString(10, Rol)
                    Crear.setInt(11, Sesion)
                    Crear.executeUpdate()

                    val Notificacion = ObjConexion?.prepareStatement("INSERT INTO TbNotificaciones (UUID_Notificacion, UUID_User, Titulo, Mensaje, Tiempo_Envio, Fecha_Envio) VALUES (?, ?, ?, ?, ?, ?)")!!

                    val deviceDetails = getDeviceDetails()
                    Notificacion.setString(1, UUID.randomUUID().toString())
                    Notificacion.setString(2, UUID_User)
                    Notificacion.setString(3, "Bienvenido/a")
                    Notificacion.setString(4, "¡Bienvenido/a a Bloming, nuestra plataforma de venta de flores en línea!")
                    Notificacion.setString(5, deviceDetails.time)
                    Notificacion.setString(6, deviceDetails.date)
                    Notificacion.executeUpdate()
                }

                dialog.dismiss()
                Toast.makeText(applicationContext, "Se creó la cuenta exitosamente.", Toast.LENGTH_SHORT).show()
                AbrirVenSingIn()
                enviarCorreo(email)
            }
        }

        btnCancelar.setOnClickListener {
            LimpiarCampos()
            dialog.dismiss()
        }

        dialog.show()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    abrirCamara()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
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
                    "${this.packageName}.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK)
        } else {
            Toast.makeText(this, "No application available to pick an image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imgPerfil = dialogView.findViewById<ImageView>(R.id.ImgPerfil_reg)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    currentPhotoPath?.let {
                        val fileUri = Uri.fromFile(File(it))
                        selectedImageUri = fileUri // Actualizar selectedImageUri con la URI de la cámara
                        Glide.with(this)
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgPerfil)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val fileUri = data?.data
                    selectedImageUri = fileUri // Actualizar selectedImageUri con la URI de la galería
                    Glide.with(this)
                        .load(fileUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgPerfil)
                }
            }
        }
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
            val storageRef = FirebaseStorage.getInstance().reference.child("Usuarios/$fileName.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
    }

    private fun hashSHA256(contrasenaEscrita: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
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

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_CAMERA_PERMISSION = 100
    }

    fun enviarCorreo(destinatario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = """
                <html>
                <body>
                    <p>Le damos una cordial bienvenida a Blooming, nuestro servicio de pedidos en línea. A través de la aplicación, 
                    podrá realizar pedidos, consultar nuestro catálogo de productos y explorar las ofertas disponibles. 
                    Agradecemos su interés y esperamos que disfrute de su experiencia con nosotros.</p>
                    <br>
                    <footer style="text-align: center; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px;">
                        <strong>Soporte de Blooming</strong>
                        <p>Ubicación: San Salvador, El Salvador</p>
                        <p>Correo: <a href="mailto:bloomingservicee@gmail.com">bloomingservicee@gmail.com</a></p>
                        <p>Síguenos en nuestras redes sociales:</p>
                        <p>
                            <a href="https://www.instagram.com/_sistema_blooming?igsh=aWRtOWZ4cHZsMnli" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/15713/15713420.png" alt="Facebook" width="24" height="24"/>
                            </a>
                            <a href="https://x.com/SistemaBlooming" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/5968/5968830.png" alt="Twitter" width="24" height="24"/>
                            </a>
                            <a href="https://www.tiktok.com/@sistema_blooming?_t=8oRwbbrEw6g&_r=1" target="_blank">
                                <img src="https://cdn-icons-png.flaticon.com/128/15713/15713399.png" alt="Instagram" width="24" height="24"/>
                            </a>
                        </p>
                    </footer>
                </body>
                </html>
            """.trimIndent()

                EnvioCorreo.EnvioDeCorreo(destinatario, "Bienvenido a Blooming", message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun LimpiarCampos() {
        CampoNombres.text.clear()
        CampoApellidos.text.clear()
        CampoUsuario.text.clear()
        CampoTelefono.text.clear()
        CampoEdad.text.clear()
        CampoCorreo.text.clear()
        CampoContra.text.clear()
        CampoConfirmarContra.text.clear()
    }

    data class DeviceDateTime(val date: String, val time: String)

    private fun getDeviceDetails(): DeviceDateTime {
        val now = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val date = now.format(dateFormatter)
        val time = now.format(timeFormatter)

        return DeviceDateTime(date, time)
    }

}