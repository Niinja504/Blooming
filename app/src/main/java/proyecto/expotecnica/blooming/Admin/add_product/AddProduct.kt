package proyecto.expotecnica.blooming.Admin.add_product

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import modelo.ClaseConexion
import modelo.ImageUtils
import proyecto.expotecnica.blooming.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.UUID

class AddProduct : Fragment() {
    private lateinit var CampoNombre: EditText
    private lateinit var CampoPrecio: EditText
    private lateinit var CampoCatidad: EditText
    private var selectedFlowers: String? = null
    private var selectedDesing: String? = null
    private var selectedEvent: String? = null
    private lateinit var CampoDescripcion: EditText
    private lateinit var dialogView: View
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var IMG_Product: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_product_inventory, container, false)
        dialogView = root

        //DropDownFlowers
        val Items = listOf("Hortencias", "Rosas", "Tulipanes", "Girasoles", "Mixtas")
        val autoComplete : AutoCompleteTextView = root.findViewById(R.id.autoComplete_AddProduct_Admin)
        val Adaptador = ArrayAdapter(requireContext(), R.layout.list_item, Items)
        autoComplete.setAdapter(Adaptador)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedFlowers = adapterView.getItemAtPosition(i) as String
            Toast.makeText(requireContext(), "Flores: $selectedFlowers", Toast.LENGTH_SHORT).show()
        }

        //DropDownDesings
        val Items1 = listOf("Cajas", "Ramos", "Botellas", "Corazones", "Rosas preservadas")
        val autoComplete1 : AutoCompleteTextView = root.findViewById(R.id.autoComplete_CategoryDesing_AddProduct_Admin)
        val Adaptador1 = ArrayAdapter(requireContext(), R.layout.list_item, Items1)
        autoComplete1.setAdapter(Adaptador1)
        autoComplete1.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedDesing = adapterView.getItemAtPosition(i) as String
            Toast.makeText(requireContext(), "Diseño: $selectedDesing", Toast.LENGTH_SHORT).show()
        }

        //DropDownEvents
        val Items2 = listOf("Cumpleaños", "Aniversario", "Amor", "Nacimiento", "Condolencias")
        val autoComplete2 : AutoCompleteTextView = root.findViewById(R.id.autoComplete_CategoryEvents_AddProduct_Admin)
        val Adaptador2 = ArrayAdapter(requireContext(), R.layout.list_item, Items2)
        autoComplete2.setAdapter(Adaptador2)
        autoComplete2.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            selectedEvent = adapterView.getItemAtPosition(i) as String
            Toast.makeText(requireContext(), "Diseño: $selectedEvent", Toast.LENGTH_SHORT).show()
        }

        //Variables que se van a utilizar
        val Regresar = root.findViewById<ImageView>(R.id.Regresar_AddProduct_Inventory)
        CampoNombre = root.findViewById(R.id.txt_Nombre_AddProduct_Admin)
        CampoPrecio = root.findViewById(R.id.txt_Precio_AddProduct_Admin)
        CampoCatidad = root.findViewById(R.id.txt_CantidadBode_AddProduct_Admin)
        CampoDescripcion = root.findViewById(R.id.txt_Descripcion_AddProduct_Admin)
        IMG_Product = root.findViewById(R.id.Img_AddProduct_Admin)
        val SubirIMG = root.findViewById<ImageView>(R.id.ic_SubirIMG_AddProduct_Admin)
        val Btn_Add = root.findViewById<Button>(R.id.btn_AddProduct_Admin)

        CampoNombre.filters = arrayOf(InputFilter.LengthFilter(18))
        CampoPrecio.filters = arrayOf(InputFilter.LengthFilter(8))
        CampoDescripcion.filters = arrayOf(InputFilter.LengthFilter(2000))

        CampoNombre.requestFocus()

        Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_inventory_admin)
        }

        SubirIMG.setOnClickListener {
            mostrarDialogoSeleccionImagen()
        }

        Btn_Add.setOnClickListener {
            lifecycleScope.launch{
                if (ValidarCampos()){
                    val imageUrl = if (selectedImageUri != null) {
                        val imageBitmap = getBitmapFromUri(requireContext(),selectedImageUri!!)
                        val resizedBitmap = ImageUtils.resizeImageIfNeeded(imageBitmap)
                        uploadImageToFirebase(resizedBitmap, "Images${System.currentTimeMillis()}")
                    } else {
                        "El usuario eligio la imagen predeternimada"
                    }
                    withContext(Dispatchers.IO){
                        val ObjConexion = ClaseConexion().CadenaConexion()

                        val AddProduct = ObjConexion?.prepareStatement("INSERT INTO TbInventario (UUID_Producto, Img_Producto, Nombre_Producto, Precio_Producto, Cantidad_Bodega_Productos, Categoria_Flores, Categoria_Diseno, Categoria_Evento, Descripcion_Producto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        )!!

                        AddProduct.setString(1, UUID.randomUUID().toString())
                        AddProduct.setString(2, imageUrl)
                        AddProduct.setString(3, CampoNombre.text.toString())
                        AddProduct.setFloat(4, CampoPrecio.text.toString().toFloat())
                        AddProduct.setInt(5, CampoCatidad.text.toString().toInt())
                        AddProduct.setString(6, selectedFlowers)
                        AddProduct.setString(7, selectedDesing)
                        AddProduct.setString(8, selectedEvent)
                        AddProduct.setString(9, CampoDescripcion.text.toString())
                        AddProduct.executeUpdate()

                    }
                    LimpiarCampo()
                }
            }
        }

        return root
    }

    private fun ValidarCampos(): Boolean {
        val Nombre = CampoNombre.text.toString()
        val Precio = CampoPrecio.text.toString()
        val CantidadBodega = CampoCatidad.text.toString()
        val Descripcion = CampoDescripcion.text.toString()

        var HayErrores = false

        if (Nombre.isEmpty()) {
            CampoNombre.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoNombre.error = null
        }

        if (Precio.isEmpty()) {
            CampoPrecio.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoPrecio.error = null
        }

        if (CantidadBodega.isEmpty()) {
            CampoCatidad.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoCatidad.error = null
        }

        if (Descripcion.isEmpty()) {
            CampoDescripcion.error = "Este campo es obligatorio"
            HayErrores = true
        } else {
            CampoDescripcion.error = null
        }

        return !HayErrores
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION_AddProduct)
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
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_AddProduct)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION_AddProduct -> {
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
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK_AddProduct)
        } else {
            Toast.makeText(requireContext(), "No application available to pick an image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_AddProduct -> {
                    currentPhotoPath?.let {
                        val fileUri = Uri.fromFile(File(it))
                        selectedImageUri = fileUri
                        Glide.with(requireContext())
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(IMG_Product)
                    }
                }
                REQUEST_IMAGE_PICK_AddProduct-> {
                    val fileUri = data?.data
                    selectedImageUri = fileUri
                    Glide.with(requireContext())
                        .load(fileUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(IMG_Product)
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
            val storageRef = FirebaseStorage.getInstance().reference.child("Inventario/$fileName.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE_AddProduct = 1
        private const val REQUEST_IMAGE_PICK_AddProduct = 2
        private const val REQUEST_CAMERA_PERMISSION_AddProduct = 100
    }

    private fun LimpiarCampo(){
        CampoNombre.text.clear()
        CampoPrecio.text.clear()
        CampoCatidad.text.clear()
        CampoDescripcion.text.clear()
    }
}