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
import android.widget.Toast
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
import java.util.UUID

class AddOffers : Fragment() {
    private lateinit var campoTitulo: EditText
    private lateinit var archivoOffer: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_offers, container, false)

        // Variables que se van a utilizar
        val regresar = root.findViewById<ImageView>(R.id.Regresar_AddOffers_Offers)
        archivoOffer = root.findViewById(R.id.ArchivoIMG_Offer)
        campoTitulo = root.findViewById(R.id.txt_Titulo_AddOffers)
        val uploadImg = root.findViewById<ImageView>(R.id.Ic_upload_AddOffer)
        val agregar = root.findViewById<Button>(R.id.btn_Agregar_AddOffers)

        campoTitulo.filters = arrayOf(InputFilter.LengthFilter(12))

        campoTitulo.requestFocus()

        regresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_offers_admin)
        }

        uploadImg.setOnClickListener {
            mostrarDialogoSeleccionArchivo()
        }

        agregar.setOnClickListener {
            lifecycleScope.launch {
                if (validarCampos()) {
                    val tituloExiste = tituloExiste(campoTitulo.text.toString())
                    if (!tituloExiste) {
                        val imageUrl = if (selectedImageUri != null) {
                            val imageBitmap = getBitmapFromUri(requireContext(), selectedImageUri!!)
                            val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                            uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                        } else {
                            "El usuario eligió la imagen predeterminada"
                        }
                        // Inserción en la base de datos
                        withContext(Dispatchers.IO) {
                            val objConexion = ClaseConexion().CadenaConexion()
                            val agregar = objConexion?.prepareStatement(
                                "INSERT INTO TbOfertas (ID_Oferta, UUID_Oferta, Titulo, Img_oferta) VALUES (SEQ_Ofertas.NEXTVAL, ?, ?, ?)"
                            )!!

                            agregar.setString(1, UUID.randomUUID().toString())
                            agregar.setString(2, campoTitulo.text.toString())
                            agregar.setString(3, imageUrl)
                            agregar.executeUpdate()
                        }
                    } else {
                        campoTitulo.error = "Ya hay otra oferta con ese título"
                    }
                }
            }
        }

        return root
    }

    private fun validarCampos(): Boolean {
        val titulo = campoTitulo.text.toString()

        var hayErrores = false

        if (titulo.isEmpty()) {
            campoTitulo.error = "Este campo es obligatorio"
            hayErrores = true
        } else {
            campoTitulo.error = null
        }

        return !hayErrores
    }

    private suspend fun tituloExiste(titulo: String): Boolean {
        val sql = "SELECT COUNT(*) AS titulo_existe FROM TbOfertas WHERE Titulo = ?"
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

    private fun mostrarDialogoSeleccionArchivo() {
        val opciones = arrayOf("Archivos", "Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirDocumentos()
                    1 -> abrirGaleria()
                }
            }
            .show()
    }

    private fun abrirDocumentos() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*" // Tipo de archivos a seleccionar (imágenes en este caso)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png")) // Tipos MIME permitidos
        }
        startActivityForResult(intent, REQUEST_DOCUMENT_PICK_AddOffer)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK_AddOffer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            if (fileUri != null) {
                selectedImageUri = fileUri
                Glide.with(requireContext())
                    .load(fileUri)
                    .apply(RequestOptions.centerCropTransform())
                    .into(archivoOffer)
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
