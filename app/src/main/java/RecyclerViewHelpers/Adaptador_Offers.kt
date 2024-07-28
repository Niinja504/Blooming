package RecyclerViewHelpers

import DataClass.DataClassOffers
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.Admin.offers.DetailsOffers
import proyecto.expotecnica.blooming.R

class Adaptador_Offers(var Datos: List<DataClassOffers>, private val fragment: Fragment) : RecyclerView.Adapter<ViewHolder_Offers>() {
    fun ActualizarListado(NuevaLista: List<DataClassOffers>){
        Datos = NuevaLista
        notifyDataSetChanged()
    }

    fun ActualizarListaDespuesDeEditar(UUID: String, NuevoTitulo: String){
        val Index = Datos.indexOfFirst { it.UUID_Oferta == UUID }
        Datos[Index].Titulo = NuevoTitulo
        notifyItemChanged(Index)
    }

    fun EliminarDatos(Titulo: String, posicion: Int){
        val ListaDatos = Datos.toMutableList()
        ListaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val DeleteTitulo = ObjConexion?.prepareStatement("DELETE TbOfertas WHERE Titulo = ?")!!
            DeleteTitulo.setString(1, Titulo)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Offers{
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_offer, parent, false)
        return  ViewHolder_Offers(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Offers, posicion: Int){
        val item = Datos[posicion]
        holder.CampoTitulo.text = item.Titulo

        holder.IC_Delete.setOnClickListener{
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                EliminarDatos(item.Titulo, posicion)
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
            val context = holder.itemView.context

            val pantallaDetalle = Intent(context, DetailsOffers::class.java)
            //Mandamos los valores
            pantallaDetalle.putExtra("uuid", item.UUID_Oferta)
            pantallaDetalle.putExtra("Titulo", item.Titulo)
            pantallaDetalle.putExtra("Img_oferta", item.Img_oferta)
            context.startActivity(pantallaDetalle)
        }
    }
}