package RecyclerViewHelpers

import DataC.DataOffers_Admin
import android.app.AlertDialog
import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Adaptador_Offers_Admin (var Datos: List<DataOffers_Admin>): RecyclerView.Adapter<ViewHolder_Offers_Admin>() {
    fun ActualizarListaDespuesDeEditar(UUID: String, NuevoTitulo: String){
        val Index = Datos.indexOfFirst { it.UUID_Oferta == UUID }
        Datos[Index].Titulo = NuevoTitulo
        notifyItemChanged(Index)
    }

    fun EliminarDatos(UUID_Oferta: String, posicion: Int){
        println(UUID_Oferta)
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val DeleteTitulo = ObjConexion?.prepareStatement("DELETE TbOfertas WHERE UUID_Oferta = ?")!!
            DeleteTitulo.setString(1, UUID_Oferta)
            DeleteTitulo.executeUpdate()

            val COMMIT = ObjConexion.prepareStatement("commit")
            COMMIT.executeUpdate()
        }
        Datos = ListaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun ActualizarDato(Titulo: String, UUID: String){
        GlobalScope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val UpdateTitulo = ObjConexion?.prepareStatement("UPDATE TbOfertas SET Titulo = ? WHERE UUID = ?")!!
            UpdateTitulo.setString(1, Titulo)
            UpdateTitulo.setString(2, UUID)
            UpdateTitulo.executeUpdate()

            val COMMIT = ObjConexion.prepareStatement("COMMIT")
            COMMIT.executeUpdate()

            withContext(Dispatchers.IO){
                ActualizarListaDespuesDeEditar(UUID, Titulo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Offers_Admin {
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_offer, parent, false)
        return  ViewHolder_Offers_Admin(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Offers_Admin, posicion: Int){
        val item = Datos[posicion]
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

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")

            val cuadroTexto = EditText(context)
            cuadroTexto.setHint(item.Titulo)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar"){
                    dialog, wich ->
                ActualizarDato(cuadroTexto.text.toString(), item.UUID_Oferta)
            }

            builder.setNegativeButton("Cancelar"){
                    dialog, wich ->
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

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}