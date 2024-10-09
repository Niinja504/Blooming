package RecyclerViewHelpers

import DataC.DataOffers_Admin
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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

class Adaptador_Offers_Admin (private val contexto: Context,var Datos: List<DataOffers_Admin>): RecyclerView.Adapter<ViewHolder_Offers_Admin>() {
    private var datosFiltrados = Datos

    fun ActualizarListaDespuesDeEditar(UUID: String, NuevoTitulo: String, NuevaDescripcion: String){
        val Index = Datos.indexOfFirst { it.UUID_Oferta == UUID }
        Datos[Index].Titulo = NuevoTitulo
        Datos[Index].Descripcion = NuevaDescripcion
        notifyItemChanged(Index)
    }

    fun EliminarDatos(UUID_Oferta: String, posicion: Int){
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ObjConexion = ClaseConexion().CadenaConexion()
                val DeleteTitulo = ObjConexion?.prepareStatement("DELETE TbOfertas WHERE UUID_Oferta = ?")!!
                DeleteTitulo.setString(1, UUID_Oferta)
                DeleteTitulo.executeUpdate()

                val COMMIT = ObjConexion?.prepareStatement("commit")!!
                COMMIT.executeUpdate()

                withContext(Dispatchers.Main) {
                    Datos = ListaDatos.toList()
                    notifyItemRemoved(posicion)
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(contexto, "Error al eliminar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun ActualizarDato(Titulo: String, Descripcion: String, UUID: String){
        GlobalScope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val UpdateTitulo = ObjConexion?.prepareStatement("UPDATE TbOfertas SET Titulo = ?, Decripcion_Oferta = ? WHERE UUID_Oferta = ?")!!
            UpdateTitulo.setString(1, Titulo)
            UpdateTitulo.setString(2, Descripcion)
            UpdateTitulo.setString(3, UUID)
            UpdateTitulo.executeUpdate()

            val COMMIT = ObjConexion.prepareStatement("COMMIT")
            COMMIT?.executeUpdate()

            withContext(Dispatchers.Main){
                ActualizarListaDespuesDeEditar(UUID, Titulo, Descripcion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Offers_Admin {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_offer_admin, parent, false)
        return  ViewHolder_Offers_Admin(Vista)
    }

    override fun getItemCount() = datosFiltrados.size

    override fun onBindViewHolder(holder: ViewHolder_Offers_Admin, posicion: Int){
        val item = datosFiltrados[posicion]
        holder.CampoTitulo.text = item.Titulo

        Glide.with(holder.IMG_Archivo.context)
            .load(item.Img_oferta)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Archivo)

        holder.IC_Delete.setOnClickListener {
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto

            val context = holder.itemView.context

            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                EliminarDatos(item.UUID_Oferta, posicion)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.IC_Editar.setOnClickListener{
            val context = holder.itemView.context

            val dialogView = LayoutInflater.from(context).inflate(R.layout.ofertas_update, null)

            val Update_Nombre = dialogView.findViewById<EditText>(R.id.Txt_Nombre)
            val Update_Descripcion = dialogView.findViewById<EditText>(R.id.Txt_Descripcion)

            Update_Nombre.filters = arrayOf(InputFilter.LengthFilter(14))

            Update_Nombre.setText(item.Titulo)
            Update_Descripcion.setText(item.Descripcion)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Usuario")
            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar"){ dialog, wich ->
                val TituloOferta = Update_Nombre.text.toString()
                val DescripcionOferta = Update_Descripcion.text.toString()

                ActualizarDato(TituloOferta , DescripcionOferta, item.UUID_Oferta)

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar"){ dialog, wich ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("titulo", item.Titulo)
                putString("porcentaje", item.Porcentaje)
                putString("descripcion", item.Descripcion)
                putString("img", item.Img_oferta)
            }

            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_Offers, bundle)
        }
    }

    fun filtrar(texto: String) {
        datosFiltrados = if (texto.isEmpty()) {
            Datos
        } else {
            Datos.filter {
                it.Titulo.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}