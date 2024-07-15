package proyecto.expotecnica.blooming.Admin.add_offers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
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
import modelo.ClaseConexion
import modelo.ImageUtils
import proyecto.expotecnica.blooming.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class AddOffers : Fragment() {
    private lateinit var CampoTitulo: EditText
    private lateinit var Archivo_Offer: ImageView
    private var selectedImageUri: Uri? = null
    private var currentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_offers, container, false)

        //Variables que se van a utilizar
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddOffers_Offers)
        Archivo_Offer = root.findViewById(R.id.ArchivoIMG_Offer)
        CampoTitulo = root.findViewById(R.id.txt_Titulo_AddOffers)
        val Upload_IMG = root.findViewById<ImageView>(R.id.Ic_upload_AddOffer)
        val Agregar = root.findViewById<Button>(R.id.btn_Agregar_AddOffers)

        CampoTitulo.filters = arrayOf(InputFilter.LengthFilter(12))

        Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_offers_admin)
        }

        Upload_IMG.setOnClickListener{
            Mostrar_Dialogo_Seleccion_Archivo()
        }

        Agregar.setOnClickListener{
            lifecycleScope.launch {
                if (ValidarCampos()){
                    val tituloExiste = TituloExiste(CampoTitulo.text.toString())
                    if (!tituloExiste){
                        val imageUrl = if (selectedImageUri != null) {
                            val imageBitmap = getBitmapFromUri(requireContext(),selectedImageUri!!)
                            val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                            uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                        } else {
                            "El usuario eligio la imagen predeternimada"
                        }
                        // Inserción en la base de datos
                        withContext(Dispatchers.IO){
                            val ObjConexion = ClaseConexion().CadenaConexion()
                            val Agregar = ObjConexion?.prepareStatement("INSERT INTO TbOfertas (ID_Oferta, UUID_Oferta, Titulo, Img_oferta) VALUES (SEQ_Ofertas.NEXTVAL, ?, ?, ?)")!!

                            Agregar.setString(1, UUID.randomUUID().toString())
                            Agregar.setString(2, CampoTitulo.text.toString())
                            Agregar.setString(3, imageUrl)
                            Agregar.executeUpdate()
                        }
                    }
                    else{
                        if (tituloExiste){
                            CampoTitulo.error = "Ya hay otra oferta con ese titulo"
                        }
                    }
                }
            }
        }

        return root
    }

    private  fun ValidarCampos(): Boolean{
        val Titulo = CampoTitulo.text.toString()

        var HayErrores = false

        if (Titulo.isEmpty()){
            CampoTitulo.error = "Este campo es obligatorio"
        }
        else{
            CampoTitulo.error = null
        }

        return !HayErrores
    }

    private suspend fun TituloExiste(titulo: String): Boolean {
        val sql = "SELECT COUNT(*) AS titulo_existe FROM TbOfertas  WHERE Titulo = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()
        var tituloExiste = false

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, titulo) // Pasamos el parámetro

                val resultado = withContext(Dispatchers.IO) { statement.executeQuery() }

                if (resultado.next()) {
                    val count = resultado.getInt("titulo_existe")
                    tituloExiste = count > 0
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

        return tituloExiste
    }

    private fun Mostrar_Dialogo_Seleccion_Archivo() {
        val opciones = arrayOf("Archivos", "Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> abrirDocumentos()
                    1 -> abrirGaleria()
                }
            }
            .show()
    }


    private fun abrirDocumentos() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*" // Tipo de archivos a seleccionar (imágenes en este caso)

        val mimeTypes = arrayOf("image/jpeg", "image/png") // Tipos MIME permitidos
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, REQUEST_DOCUMENT_PICK_AddOffer)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK_AddOffer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_DOCUMENT_PICK_AddOffer -> {
                    // Handle document selection result
                    val fileUri = data?.data
                    fileUri?.let {
                        selectedImageUri = it // Update selectedImageUri with the document URI
                        Glide.with(requireContext())
                            .load(it)
                            .into(Archivo_Offer)
                    }
                }

                REQUEST_IMAGE_PICK_AddOffer -> {
                    // Handle gallery selection result
                    val fileUri = data?.data
                    selectedImageUri = fileUri // Update selectedImageUri with the gallery URI
                    Glide.with(requireContext())
                        .load(fileUri)
                        .into(Archivo_Offer)
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
            val storageRef = FirebaseStorage.getInstance().reference.child("Ofertas/$fileName.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
    }

    companion object {
        private const val REQUEST_DOCUMENT_PICK_AddOffer = 1002
        private const val REQUEST_IMAGE_PICK_AddOffer = 2
    }

}