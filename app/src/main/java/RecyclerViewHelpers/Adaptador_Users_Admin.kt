package RecyclerViewHelpers

import DataC.DataUsers
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import java.security.MessageDigest

class  Adaptador_Users_Admin (var Datos: List<DataUsers>): RecyclerView.Adapter<ViewHolder_Users_Admin>()  {
    private var datosFiltrados = Datos

    fun ActualizarListaDespuesDeEditar(uuid: String, NuevoNombre: String, NuevoUsuario: String, NuevoApellido: String, NuevoTelefono: String, NuevoCorreo: String){
        val Index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[Index].Nombres = NuevoNombre
        Datos[Index].NombreUser = NuevoUsuario
        Datos[Index].Apellidos = NuevoApellido
        Datos[Index].Num_Telefono = NuevoTelefono
        Datos[Index].Email_User = NuevoCorreo
        notifyItemChanged(Index)
    }

    fun eliminarDatos(nombreUsuario: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- creo una variable que contenga
            //un PrepareStatement
            val DeleteUser = objConexion?.prepareStatement("DELETE TbUsers WHERE UUID_User = ?")!!
            DeleteUser.setString(1, nombreUsuario)
            DeleteUser.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarDato(
        nombreUsuario: String,
        apellidoUsuario: String,
        nombreDeUsuario: String,
        telefono: String,
        email: String,
        contra: String,
        uuid: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val UpdateUsuario = ObjConexion?.prepareStatement(
                "UPDATE TbUsers SET Nombres_User = ?, Apellido_User = ?, Nombre_de_Usuario = ?, Num_Telefono_User = ?, Email_User = ?, Contra_User = ? WHERE UUID_User = ?"
            )
            UpdateUsuario?.apply {
                setString(1, nombreUsuario)
                setString(2, apellidoUsuario)
                setString(3, nombreDeUsuario)
                setString(4, telefono)
                setString(5, email)
                setString(6, contra)
                setString(7, uuid)
                executeUpdate()
            }

            withContext(Dispatchers.Main){
                ActualizarListaDespuesDeEditar(uuid, nombreUsuario, nombreDeUsuario, apellidoUsuario, telefono, email)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Users_Admin {
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_users, parent, false)
        return ViewHolder_Users_Admin(vista)
    }

    override fun getItemCount() = datosFiltrados.size

    override fun onBindViewHolder(holder: ViewHolder_Users_Admin, position: Int) {
        val item = datosFiltrados[position]
        holder.Nombre_Usuario.text = item.Nombres
        holder.NomberDeUsuario.text = item.NombreUser
        holder.RolUsuario.text = item.Rol

        Glide.with(holder.IMG_User_View.context)
            .load(item.IMG_User)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_User_View)

        //Todo: clic al icono de borrar
        holder.IC_Eliminar.setOnClickListener {
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto

            val context = holder.itemView.context

            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                eliminarDatos(item.uuid, position)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //TODO: ClIC AL ICONO DE EDITAR
        holder.IC_Editar.setOnClickListener {
            val context = holder.itemView.context

            val dialogView = LayoutInflater.from(context).inflate(R.layout.usuarios_actualizar, null)

            val Update_Nombres = dialogView.findViewById<EditText>(R.id.Txt_Nombres)
            val Update_Apellidos = dialogView.findViewById<EditText>(R.id.Txt_Apellidos)
            val Update_NombreUsuario = dialogView.findViewById<EditText>(R.id.Txt_NombreUsuario)
            val Update_Telefono = dialogView.findViewById<EditText>(R.id.Txt_NumTelefono)
            val Update_Correo = dialogView.findViewById<EditText>(R.id.Txt_Correo)
            val Update_Contra = dialogView.findViewById<EditText>(R.id.Txt_Contra)

            Update_Telefono.addTextChangedListener(TelefonoTextWatcher(Update_Telefono))

            val ContrasenaEncriptada: String = hashSHA256(Update_Contra.text.toString())

            Update_Nombres.filters = arrayOf(InputFilter.LengthFilter(15))
            Update_Apellidos.filters = arrayOf(InputFilter.LengthFilter(15))
            Update_NombreUsuario.filters = arrayOf(InputFilter.LengthFilter(15))
            Update_Telefono.filters = arrayOf(InputFilter.LengthFilter(11))
            Update_Telefono.inputType = InputType.TYPE_CLASS_NUMBER
            Update_Correo.filters = arrayOf(InputFilter.LengthFilter(35))
            Update_Contra.filters = arrayOf(InputFilter.LengthFilter(30))

            Update_Nombres.setText(item.Nombres)
            Update_Apellidos.setText(item.Apellidos)
            Update_NombreUsuario.setText(item.NombreUser)
            Update_Telefono.setText(item.Num_Telefono)
            Update_Correo.setText(item.Email_User)
            Update_Contra.setText("")

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Usuario")
            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar") { dialog, _ ->
                val nombreUsuario = Update_Nombres.text.toString()
                val apellidoUsuario = Update_Apellidos.text.toString()
                val nombreDeUsuario = Update_NombreUsuario.text.toString()
                val telefono = Update_Telefono.text.toString()
                val email = Update_Correo.text.toString()
                val contra = ContrasenaEncriptada

                actualizarDato(nombreUsuario, apellidoUsuario, nombreDeUsuario, telefono, email, contra, item.uuid, GlobalScope)

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nombres", item.Nombres)
                putString("apellidos", item.Apellidos)
                putString("nombre_usuario", item.NombreUser)
                putString("num_telefono", item.Num_Telefono)
                putString("correo_usuario", item.Email_User)
                putString("contra", item.Contra)
                putString("img", item.IMG_User)
                putString("Rol", item.Rol)
                putInt("sesion_user", item.Sesion_User)
            }
            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_Users, bundle)
        }
    }

    fun filtrar(texto: String) {
        datosFiltrados = if (texto.isEmpty()) {
            Datos
        } else {
            Datos.filter {
                it.Nombres.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private fun hashSHA256(contrasenaEscrita: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    inner class TelefonoTextWatcher(private val editText: EditText) : TextWatcher {
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

            editText.setText(formatted.toString())
            editText.setSelection(formatted.length)

            isUpdating = false
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}