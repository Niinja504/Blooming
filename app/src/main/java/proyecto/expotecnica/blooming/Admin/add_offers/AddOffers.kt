package proyecto.expotecnica.blooming.Admin.add_offers

import DataC.DataListProducts_Admin
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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.EnvioCorreo
import modelo.ImageUtils
import proyecto.expotecnica.blooming.R
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.util.UUID

class AddOffers : Fragment() {
    private lateinit var campoTitulo: EditText
    private var selectedProduct: DataListProducts_Admin? = null
    private lateinit var campoPorcentaje: EditText
    private lateinit var campoDescripcion: EditText
    private lateinit var archivoOffer: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_offers, container, false)
        dialogView = root

        lifecycleScope.launch {
            val items = NombresProductos()
            setupAutoCompleteTextView(root, items)
        }

        // Variables que se van a utilizar
        val regresar = root.findViewById<ImageView>(R.id.Regresar_AddOffers_Offers)
        archivoOffer = root.findViewById(R.id.ArchivoIMG_Offer)
        campoTitulo = root.findViewById(R.id.txt_Titulo_AddOffers)
        campoPorcentaje = root.findViewById(R.id.txt_Porcentaje_AddOffers)
        campoDescripcion = root.findViewById(R.id.txt_Descripcion_AddOffers)
        val uploadImg = root.findViewById<ImageView>(R.id.Ic_upload_AddOffer)
        val agregar = root.findViewById<Button>(R.id.btn_Agregar_AddOffers)
        val ProgessBar = root.findViewById<ProgressBar>(R.id.progressBar_Add_Offers_Admin)

        campoTitulo.filters = arrayOf(InputFilter.LengthFilter(30))
        campoPorcentaje.filters = arrayOf(InputFilter.LengthFilter(3))
        setupPercentageField(campoPorcentaje)
        campoDescripcion.filters = arrayOf(InputFilter.LengthFilter(400))

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
                    val titulo = campoTitulo.text.toString()
                    val tituloExiste = tituloExiste(titulo)
                    if (!tituloExiste) {
                        Toast.makeText(requireContext(), "Por favor, no cierre la aplicación, ya que se está subiendo la oferta. Gracias.", Toast.LENGTH_SHORT).show()

                        ProgessBar.visibility = View.VISIBLE
                        requireView().alpha = 0.5f

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
                                "INSERT INTO TbOfertas (UUID_Oferta, UUID_Producto, Titulo, Porcentaje_Oferta, Decripcion_Oferta, Img_oferta) VALUES (?, ?, ?, ?, ?, ?)"
                            )!!

                            agregar.setString(1, UUID.randomUUID().toString())
                            agregar.setString(2, selectedProduct!!.uuid)
                            agregar.setString(3, titulo)
                            agregar.setString(4, campoPorcentaje.text.toString())
                            agregar.setString(5, campoDescripcion.text.toString())
                            agregar.setString(6, imageUrl)
                            agregar.executeUpdate()
                        }

                        val correos = obtenerCorreosClientes()
                        Toast.makeText(requireContext(), "Se subido la oferta exitosamente.", Toast.LENGTH_SHORT).show()
                        LimpiarCampos()
                        findNavController().navigate(R.id.navigation_offers_admin)
                        if (correos.isNotEmpty()) {
                            enviarCorreos(correos, titulo, campoPorcentaje.text.toString(), campoDescripcion.text.toString(), imageUrl.toString())
                        }
                        ProgessBar.visibility = View.GONE
                        requireView().alpha = 1f

                    } else {
                        campoTitulo.error = "Ya hay otra oferta con ese título"
                    }
                }
            }
        }

        return root
    }

    private fun setupAutoCompleteTextView(root: View, items: List<DataListProducts_Admin>) {
        val autoComplete: AutoCompleteTextView = root.findViewById(R.id.autoComplete_AddOffers_Admin)
        val adaptador = ArrayAdapter(requireContext(), R.layout.list_item, items.map { it.nombre }) // Obtener solo los nombres =)
        autoComplete.setAdapter(adaptador)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, position, _ ->
            selectedProduct = items[position]
            selectedProduct?.uuid
            Toast.makeText(requireContext(), "Flores: ${selectedProduct?.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun NombresProductos(): List<DataListProducts_Admin> {
        return withContext(Dispatchers.IO) {
            val nombresProductos = mutableListOf<DataListProducts_Admin>()

            val conexion = ClaseConexion().CadenaConexion()
            conexion?.let {
                try {
                    val query = "SELECT UUID_Producto, Nombre_Producto FROM TbInventario"
                    val statement = it.createStatement()
                    val resultSet = statement.executeQuery(query)

                    while (resultSet.next()) {
                        val uuid = resultSet.getString("UUID_Producto")
                        val nombreProducto = resultSet.getString("Nombre_Producto")
                        nombresProductos.add(DataListProducts_Admin(uuid, nombreProducto))
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    it.close()
                }
            }
            nombresProductos
        }
    }

    private fun validarCampos(): Boolean {
        val titulo = campoTitulo.text.toString()
        val porcentaje = campoPorcentaje.text.toString()
        val descripcion = campoDescripcion.text.toString()

        var hayErrores = false

        if (titulo.isEmpty()) {
            campoTitulo.error = "Este campo es obligatorio"
            hayErrores = true
        } else {
            campoTitulo.error = null
        }

        val Simbolo = porcentaje.replace("%", "").trim()

        if (Simbolo.isEmpty() || Simbolo.toIntOrNull() == null) {
            campoPorcentaje.error = "Este campo es obligatorio"
            hayErrores = true
        } else {
            campoPorcentaje.error = null
        }

        if (descripcion.isEmpty()) {
            campoDescripcion.error = "Este campo es obligatorio"
            hayErrores = true
        } else {
            campoDescripcion.error = null
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

    private suspend fun obtenerCorreosClientes(): List<String> {
        val sql = "SELECT Email_User FROM TbUsers WHERE Rol_User = ?"
        val claseConexion = ClaseConexion()
        val conexion = claseConexion.CadenaConexion()
        val correos = mutableListOf<String>()

        if (conexion != null) {
            try {
                val statement = withContext(Dispatchers.IO) { conexion.prepareStatement(sql) }
                statement.setString(1, "Cliente")

                val resultado = withContext(Dispatchers.IO) { statement.executeQuery() }

                while (resultado.next()) {
                    val email = resultado.getString("Email_User")
                    correos.add(email)
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

        return correos
    }

    fun enviarCorreos(correos: List<String>, titulo: String, porcentaje: String, descripcion: String, imageUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messageTemplate = """
            <html>
            <body>
                <p>Estimado/a cliente:
                   Nos complace informarle sobre una oferta especial por tiempo limitado de nuestra innovadora aplicación de pedidos en línea. 
                   No pierda la oportunidad de beneficiarse de esta promoción. Si desea más información, no dude en contactarnos.
                   <h3>Detalles de la oferta:</h3>
                   <p><strong>Título:</strong> $titulo</p>
                   <p><strong>Porcentaje de descuento:</strong> $porcentaje</p>
                   <p><strong>Descripción:</strong> $descripcion</p>
                   <p><strong>Imagen de la oferta:</strong></p>
                   <img src="$imageUrl" alt="Imagen de la oferta" style="max-width: 100%; height: auto;"/>
                   <p>Si desea más información, no dude en contactarnos.</p>
                   Atentamente : Equipo de ventas de Blooming</p>
                </body>
            </html>
            """.trimIndent()

                for (correo in correos) {
                    EnvioCorreo.EnvioDeCorreo(correo, "Nueva oferta", messageTemplate)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
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

    fun setupPercentageField(campoPorcentaje: EditText) {
        campoPorcentaje.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se hace nada aqui xd
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No se hace nada aqui xd
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                if (text.length > 1 && text.matches(Regex("\\d{2}")) && !text.endsWith("%")) {
                    campoPorcentaje.removeTextChangedListener(this)

                    val newText = text + "%"
                    campoPorcentaje.setText(newText)
                    campoPorcentaje.setSelection(newText.length)

                    campoPorcentaje.addTextChangedListener(this)
                }
            }
        })
    }

    companion object {
        private const val REQUEST_DOCUMENT_PICK_AddOffer = 1002
        private const val REQUEST_IMAGE_PICK_AddOffer = 2
    }

    private fun LimpiarCampos(){
        campoTitulo.text.clear()
        campoPorcentaje.text.clear()
        campoDescripcion.text.clear()
    }
}
