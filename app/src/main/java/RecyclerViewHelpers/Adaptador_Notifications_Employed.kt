package RecyclerViewHelpers

import DataC.Data_Notifications
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R

class Adaptador_Notifications_Employed (var Datos: List<Data_Notifications>): RecyclerView.Adapter<ViewHolder_Notifications_Employed>() {
    private var datosFiltrados = Datos

    fun eliminarDatos(Titulo: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- creo una variable que contenga
            //un PrepareStatement
            val DeleteMensaje = objConexion?.prepareStatement("DELETE TbNotificaciones WHERE UUID_Notificacion = ?")!!
            DeleteMensaje.setString(1, Titulo)
            DeleteMensaje.executeUpdate()

            objConexion?.commit()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Notifications_Employed {
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_notifications_employed, parent, false)
        return ViewHolder_Notifications_Employed(vista)
    }

    override fun getItemCount() = datosFiltrados.size

    override fun onBindViewHolder(holder: ViewHolder_Notifications_Employed, position: Int) {
        val item = datosFiltrados[position]
        holder.Titulo.text = item.Titulo
        holder.Mensaje.text = item.Mensaje
        holder.Tiempo.text = item.Tiempo
        holder.Fecha.text = item.Fecha

        //Todo: clic al icono de borrar
        holder.IC_Eliminar.setOnClickListener {
            eliminarDatos(item.UUID, position)
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
}