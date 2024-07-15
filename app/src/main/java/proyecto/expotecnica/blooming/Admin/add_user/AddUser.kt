package proyecto.expotecnica.blooming.Admin.add_user

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import modelo.AuFi
import modelo.ClaseConexion
import modelo.ImageUtils
import proyecto.expotecnica.blooming.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID

class AddUser : Fragment() {
    private lateinit var CampoNombres: EditText
    private lateinit var CampoApellidos: EditText
    private lateinit var CampoCorreo: EditText
    private lateinit var CampoContra: EditText
    private lateinit var CampoConfirmarContra: EditText
    private lateinit var CampoEdad: EditText
    private lateinit var CampoTelefono: EditText
    private lateinit var dialogView: View
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var IMG_Perfil: ImageView
    private var selectedRole: String? = null  // Variable para almacenar el rol seleccionado


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_user_admin, container, false)
        dialogView = root

        //DropDown
        val Items = listOf("Administrador", "Empleado")
        val autoComplete : AutoCompleteTextView = root.findViewById(R.id.autoComplete_AddUser_Admin)
        val Adaptador = ArrayAdapter(requireContext(), R.layout.list_item, Items)
        autoComplete.setAdapter(Adaptador)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedRole = adapterView.getItemAtPosition(i) as String
            Toast.makeText(requireContext(), "Item: $selectedRole", Toast.LENGTH_SHORT).show()
        }

        //Variables que se van a utilizar
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddUser_Admin)
        CampoNombres = root.findViewById(R.id.txt_Nombres_Reg_Admin)
        CampoApellidos = root.findViewById(R.id.txt_Apellidos_Reg_Admin)
        CampoCorreo = root.findViewById(R.id.txt_Correo_Reg_Admin)
        CampoContra = root.findViewById(R.id.txt_Contra_Reg_Admin)
        CampoConfirmarContra = root.findViewById(R.id.txt_ConfirmarContra_Reg_Admin)
        CampoEdad = root.findViewById(R.id.txt_Edad_Reg_Admin)
        CampoTelefono = root.findViewById(R.id.txt_Telefono_Reg_Admin)
        IMG_Perfil = root.findViewById(R.id.Img_AddUser_Admin)
        val SubirFoto = root.findViewById<ImageView>(R.id.ic_SubirIMG_AddUser_Admin)
        val Btn_CrearCuenta = root.findViewById<Button>(R.id.btn_AgregarUser_Admin)

        CampoNombres.filters = arrayOf(InputFilter.LengthFilter(15))
        CampoApellidos.filters = arrayOf(InputFilter.LengthFilter(15))
        CampoCorreo.filters = arrayOf(InputFilter.LengthFilter(30))
        CampoContra.filters = arrayOf(InputFilter.LengthFilter(20))
        CampoConfirmarContra.filters = arrayOf(InputFilter.LengthFilter(20))
        CampoTelefono.filters = arrayOf(InputFilter.LengthFilter(11))
        CampoTelefono.inputType = InputType.TYPE_CLASS_NUMBER
        CampoTelefono.addTextChangedListener(TelefonoTextWatcher())

        CampoNombres.requestFocus()

        Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_users_admin)
        }

        SubirFoto.setOnClickListener{
            mostrarDialogoSeleccionImagen()
        }

        Btn_CrearCuenta.setOnClickListener{
            lifecycleScope.launch {
                if (ValidarCampos()){
                    val correoExiste = CorreoExiste(CampoCorreo.text.toString())
                    if (!correoExiste){
                        val imageUrl = if (selectedImageUri != null) {
                            val imageBitmap = getBitmapFromUri(requireContext(),selectedImageUri!!)
                            val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                            uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                        } else {
                            "El usuario eligio la imagen predeternimada"
                        }

                        // Inserción en la base de datos
                        withContext(Dispatchers.IO) {
                            val ObjConexion = ClaseConexion().CadenaConexion()

                            // Encripto la contraseña
                            val ContraEncrip = hashSHA256(CampoContra.text.toString())

                            val Crear = ObjConexion?.prepareStatement(
                                "INSERT INTO TbUSers_Employed_Admin (ID_User_Employed, UUID_Employed_Admin, Img_Employed_Admin, Nombres_Employed_Admin, Apellidos_Employed_Admin, Correo_Employed_Admin, Contra_Employed_Admin, Edad_Employed_Admin, Num_Telefono_Employed_Admin, Rol_Employed_Admin) VALUES (SEQ_USers_Employed_Admin.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                            )!!

                            Crear.setString(1, UUID.randomUUID().toString())
                            Crear.setString(2, imageUrl)
                            Crear.setString(3, CampoNombres.text.toString())
                            Crear.setString(4, CampoApellidos.text.toString())
                            Crear.setString(5, CampoCorreo.text.toString())
                            Crear.setString(6, ContraEncrip)
                            Crear.setString(7, CampoEdad.text.toString())
                            Crear.setString(8, CampoTelefono.text.toString())
                            Crear.setString(9, selectedRole)
                            Crear.executeUpdate()
                        }
                    }
                    else{
                        if (correoExiste) {
                            CampoCorreo.error = "El correo ya existe"
                        }
                    }
                }
            }
        }


        return root
    }

    private fun ValidarCampos(): Boolean {
        val Nombre = CampoNombres.text.toString()
        val Apellido = CampoApellidos.text.toString()
        val Correo = CampoCorreo.text.toString()
        val Contra = CampoContra.text.toString()
        val VerificarContra = CampoConfirmarContra.text.toString()
        val Edad = CampoEdad.text.toString()
        val Telefono = CampoTelefono.text.toString()

        var HayErrores = false

        if (Nombre.isEmpty()) {
            CampoNombres.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombres.error = null
        }

        if (Apellido.isEmpty()) {
            CampoNombres.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombres.error = null
        }

        if (!Correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.][a-z]+"))) {
            CampoCorreo.error = "El correo no tiene el formato válido"
            HayErrores = true
        } else {
            CampoCorreo.error = null
        }

        if (Contra.isEmpty()) {
            CampoNombres.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombres.error = null
        }

        if (VerificarContra.isEmpty()) {
            CampoConfirmarContra.error = "Este campo es obligatorio para verificar su contraseña"
            HayErrores = true
        } else if (VerificarContra != Contra) {
            CampoConfirmarContra.error = "Las contraseñas no coinciden"
            HayErrores = true
        } else {
            CampoConfirmarContra.error = null
        }

        if (Contra.length <= 8) {
            CampoContra.error = "La contraseña debe tener más de 8 caracteres"
            HayErrores = true
        } else {
            CampoContra.error = null
        }

        if (Edad.isEmpty()) {
            CampoNombres.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombres.error = null
        }
        if (Telefono.length < 8) {
            CampoTelefono.error = "El número telefonico debe de tener 8 digitos"
            HayErrores = true
        } else {
            CampoContra.error = null
        }
        return !HayErrores
    }

    private suspend fun CorreoExiste(correo: String): Boolean {
        val sql = "SELECT COUNT(*) AS correo_existe FROM TbUSers_Employed_Admin  WHERE Correo_Employed_Admin= ?"
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

    private fun mostrarDialogoSeleccionImagen() {
        val opciones = arrayOf("Cámara", "Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> abrirGaleria()
                }
            }
            .show()
    }

    ////////////////////////////////
    private fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no está concedido, solicitar el permiso
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_AddUser)
        } else {
            // El permiso está concedido, abrir la cámara
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File? = try {
                ImageUtils.createImageFile(requireContext())
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                currentPhotoPath = it.absolutePath
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_AddUser)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION_AddUser -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // El permiso ha sido concedido, abrir la cámara
                    abrirCamara()
                } else {
                    // El permiso ha sido denegado, mostrar un mensaje o manejarlo apropiadamente
                    Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK_AddUser) // Corrected request code
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_AddUser -> {
                    // Handle camera capture result
                    currentPhotoPath?.let {
                        val fileUri = Uri.fromFile(File(it))
                        selectedImageUri = fileUri // Update selectedImageUri with the camera URI
                        Glide.with(requireContext())
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(IMG_Perfil)
                    }
                }
                REQUEST_IMAGE_PICK_AddUser -> {
                    // Handle gallery selection result
                    val fileUri = data?.data
                    selectedImageUri = fileUri // Update selectedImageUri with the gallery URI
                    Glide.with(requireContext())
                        .load(fileUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(IMG_Perfil)
                }
            }
        }
    }


    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private suspend fun uploadImageToFirebase(bitmap: Bitmap, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().reference.child("Admin_Empleados/$fileName.jpg")
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
        private const val REQUEST_IMAGE_CAPTURE_AddUser = 1
        private const val REQUEST_IMAGE_PICK_AddUser = 2
        private const val REQUEST_CAMERA_PERMISSION_AddUser = 100
    }
}