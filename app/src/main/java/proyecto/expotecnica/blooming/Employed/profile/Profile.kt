package proyecto.expotecnica.blooming.Employed.profile

import DataC.DataUsers
import DataC.Data_Profile
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ImageUtils
import proyecto.expotecnica.blooming.Employed.ImageViewModel_Employed
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.Sing_in
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class Profile : Fragment() {
    private val imageViewModel: ImageViewModel_Employed by activityViewModels()
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var IMG_Perfil: ImageView
    private var UserData: Data_Profile? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_profile_employed, container, false)

        IMG_Perfil = root.findViewById(R.id.IMG_User_Profile_Employed)
        val ChangePassword = root.findViewById<ImageView>(R.id.IC_ChangePassword_Employed)
        val IC_Settings = root.findViewById<ImageView>(R.id.IC_Settings_Employed)
        val lbl_Nombre = root.findViewById<TextView>(R.id.lbl_Nombre_User_Profile_Employed)
        val lbl_Apellido = root.findViewById<TextView>(R.id.lbl_Apellido_User_Profile_Employed)
        val lbl_Nombre_De_Usuario = root.findViewById<TextView>(R.id.lbl_Nombre_De_Usuario_Profile_Employed)
        val lbl_Edad = root.findViewById<TextView>(R.id.lbl_Edad_Profile_Employed)
        val lbl_Telefono = root.findViewById<TextView>(R.id.lbl_Telefono_Profile_Employed)

        val btn_upload = root.findViewById<ImageView>(R.id.Ic_upload_Profile_Employed)
        val btn_Edit = root.findViewById<ImageView>(R.id.Ic_Edit_Profile_Employed)
        val btn_Cerrar = root.findViewById<Button>(R.id.btn_CerrarSesion_Profile_Employed)

        imageViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { imageUrl ->
                Glide.with(IMG_Perfil.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(IMG_Perfil)
            }
        }

        imageViewModel.uuid.observe(viewLifecycleOwner) { uuid ->
            uuid?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = fetchUserDataByUUID(it)
                    user?.let { userData ->
                        UserData = Data_Profile(
                            Nombres = userData.Nombres,
                            Apellidos = userData.Apellidos,
                            NombreUser = userData.NombreUser,
                            Num_Telefono = userData.Num_Telefono,
                            Email_User = userData.Email_User
                        )

                        lbl_Nombre.text = userData.Nombres
                        lbl_Apellido.text = userData.Apellidos
                        lbl_Nombre_De_Usuario.text = userData.NombreUser
                        lbl_Edad.text = userData.Edad?.toString()
                        lbl_Telefono.text = userData.Num_Telefono

                        Glide.with(IMG_Perfil.context)
                            .load(userData.IMG_User)
                            .placeholder(R.drawable.profile_user)
                            .error(R.drawable.profile_user)
                            .into(IMG_Perfil)
                    }
                }
            }
        }


        ChangePassword.setOnClickListener{
            val uuid = imageViewModel.uuid.value
            val Correo = imageViewModel.email.value
            val bundle = Bundle().apply {
                putString("UUID", uuid)
                putString("Correo", Correo)
            }
            findNavController().navigate(R.id.action_ChangePassword_Employed, bundle)
        }

        IC_Settings.setOnClickListener {
            findNavController().navigate(R.id.action_Setting_Employed)
        }

        btn_Edit.setOnClickListener {
            UserData?.let {
                Update(it)
            }
        }

        btn_upload.setOnClickListener {
            mostrarDialogoSeleccionImagen()
        }

        btn_Cerrar.setOnClickListener {
            CerrarSesion()
        }

        return root
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

    private fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_Update_Profile_Employed)
        } else {
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
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_Update_Profile_Employed)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION_Update_Profile_Employed -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    abrirCamara()
                } else {
                    Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK_Update_Profile_Employed)
        } else {
            Toast.makeText(requireContext(), "No application available to pick an image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_Update_Profile_Employed -> {
                    currentPhotoPath?.let {
                        val fileUri = Uri.fromFile(File(it))
                        selectedImageUri = fileUri
                        Glide.with(requireContext())
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(IMG_Perfil)

                        lifecycleScope.launch {
                            val imageBitmap = getBitmapFromUri(requireContext(), selectedImageUri!!)
                            val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                            val imageUrl = uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")

                            if (imageUrl != null) {
                                withContext(Dispatchers.IO) {
                                    val ObjConexion = ClaseConexion().CadenaConexion()
                                    val Update = ObjConexion?.prepareStatement("UPDATE TbUsers SET Img_User = ? WHERE UUID_User = ?")!!
                                    Update.setString(1, imageUrl)
                                    Update.setString(2, imageViewModel.uuid.value)
                                    Update.executeUpdate()
                                }
                                Toast.makeText(requireContext(), "Su foto se ha actualizado", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                REQUEST_IMAGE_PICK_Update_Profile_Employed -> {
                    val fileUri = data?.data
                    selectedImageUri = fileUri
                    Glide.with(requireContext())
                        .load(fileUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(IMG_Perfil)

                    lifecycleScope.launch {
                        val imageBitmap = getBitmapFromUri(requireContext(), selectedImageUri!!)
                        val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                        val imageUrl = uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")

                        if (imageUrl != null) {
                            withContext(Dispatchers.IO) {
                                val ObjConexion = ClaseConexion().CadenaConexion()
                                val Update = ObjConexion?.prepareStatement("UPDATE TbUsers SET Img_User = ? WHERE UUID_User = ?")!!
                                Update.setString(1, imageUrl)
                                Update.setString(2, imageViewModel.uuid.value)
                                Update.executeUpdate()
                            }
                            Toast.makeText(requireContext(), "Su foto se ha actualizado", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show()
                        }
                    }
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
            val storageRef = FirebaseStorage.getInstance().reference.child("Usuarios/$fileName.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            try {
                val uploadTask = storageRef.putBytes(data).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                downloadUrl.toString()
            } catch (e: Exception) {
                null
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE_Update_Profile_Employed = 1
        private const val REQUEST_IMAGE_PICK_Update_Profile_Employed = 2
        private const val REQUEST_CAMERA_PERMISSION_Update_Profile_Employed = 100
    }

    fun Update(UserData: Data_Profile) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.update_profile, null)

        val Update_Nombres = dialogView.findViewById<EditText>(R.id.txt_Nombres)
        val Update_Apellidos = dialogView.findViewById<EditText>(R.id.txt_Apellidos)
        val Update_NombreUsuario = dialogView.findViewById<EditText>(R.id.txt_NombreUsuario)
        val Update_Telefono = dialogView.findViewById<EditText>(R.id.txt_NumTelefono)
        val Update_Correo = dialogView.findViewById<EditText>(R.id.txt_Correo)

        Update_Nombres.setText(UserData.Nombres)
        Update_Apellidos.setText(UserData.Apellidos)
        Update_NombreUsuario.setText(UserData.NombreUser)
        Update_Telefono.setText(UserData.Num_Telefono)
        Update_Correo.setText(UserData.Email_User)

        Update_Nombres.filters = arrayOf(InputFilter.LengthFilter(15))
        Update_Apellidos.filters = arrayOf(InputFilter.LengthFilter(15))
        Update_NombreUsuario.filters = arrayOf(InputFilter.LengthFilter(15))
        Update_Telefono.filters = arrayOf(InputFilter.LengthFilter(11))
        Update_Telefono.inputType = InputType.TYPE_CLASS_NUMBER
        Update_Correo.filters = arrayOf(InputFilter.LengthFilter(35))

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Actualizar Usuario")
        builder.setView(dialogView)

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton("Actualizar") { dialog, _ ->
            val updatedNombres = Update_Nombres.text.toString()
            val updatedApellidos = Update_Apellidos.text.toString()
            val updatedNombreUsuario = Update_NombreUsuario.text.toString()
            val updatedTelefono = Update_Telefono.text.toString()
            val updatedCorreo = Update_Correo.text.toString()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val updateStatement = ObjConexion?.prepareStatement("UPDATE TbUsers SET Nombres_User = ?, Apellido_User = ?, Nombre_de_Usuario = ?, Num_Telefono_User = ?, Email_User = ? WHERE UUID_User = ?")!!
                    updateStatement?.apply {
                        setString(1, updatedNombres)
                        setString(2, updatedApellidos)
                        setString(3, updatedNombreUsuario)
                        setString(4, updatedTelefono)
                        setString(5, updatedCorreo)
                        setString(6, imageViewModel.uuid.value)
                        executeUpdate()
                    }
                }
                Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        builder.create().show()
    }

    fun CerrarSesion(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val ObjConexion = ClaseConexion().CadenaConexion()
                    val Cerrar = ObjConexion?.prepareStatement("UPDATE TbUsers SET Sesion_User = ? WHERE UUID_User = ?")!!
                    val Cerrado = 0

                    Cerrar.setInt(1, Cerrado)
                    Cerrar.setString(2,imageViewModel.uuid.value)
                    Cerrar.executeUpdate()
                }

                withContext(Dispatchers.Main) {
                    requireActivity().finish()
                    val pantallaSing_In = Intent(requireContext(), Sing_in::class.java)
                    startActivity(pantallaSing_In)
                }
            }

        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private suspend fun fetchUserDataByUUID(uuid: String): DataUsers? {
        return withContext(Dispatchers.IO) {
            var user: DataUsers? = null
            var connection: Connection? = null
            var resultSet: ResultSet? = null
            var statement: Statement? = null

            try {
                connection = ClaseConexion().CadenaConexion()
                val query = "SELECT Nombres_User, Apellido_User, Nombre_de_Usuario, Num_Telefono_User, Edad_User, Email_User, Img_User, Contra_User, Rol_User, Sesion_User FROM TbUsers WHERE UUID_User = ?"
                val preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setString(1, uuid)
                resultSet = preparedStatement?.executeQuery()

                if (resultSet?.next() == true) {
                    user = DataUsers(
                        uuid = uuid,
                        Nombres = resultSet.getString("Nombres_User"),
                        Apellidos = resultSet.getString("Apellido_User"),
                        NombreUser = resultSet.getString("Nombre_de_Usuario"),
                        Num_Telefono = resultSet.getString("Num_Telefono_User"),
                        Edad = resultSet.getInt("Edad_User"),
                        Email_User = resultSet.getString("Email_User"),
                        Contra = resultSet.getString("Contra_User"),
                        IMG_User = resultSet.getString("Img_User"),
                        Rol = resultSet.getString("Rol_User"),
                        Sesion_User = resultSet.getInt("Sesion_User")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                resultSet?.close()
                statement?.close()
                connection?.close()
            }
            user
        }
    }
}