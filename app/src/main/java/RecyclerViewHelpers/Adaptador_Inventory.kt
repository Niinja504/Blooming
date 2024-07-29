package RecyclerViewHelpers

import DataC.DataInventory
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
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

class Adaptador_Inventory (var Datos: List<DataInventory>): RecyclerView.Adapter<ViewHolder_Inventory>() {
    fun ActualizarListaDespuesDeEditar(uuid: String, NuevoNombre: String, NuevoPrecio: Float, NuevaCantidadBode: Int, NuevaCategoriaFlores: String, NuevaCategoriaDiseno: String, NuevaCategoriaEventos: String, NuevaDescripcion: String){
        val Index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[Index].Nombre = NuevoNombre
        Datos[Index].Precio = NuevoPrecio
        Datos[Index].CantidadBode = NuevaCantidadBode
        Datos[Index].CategoriaFlores = NuevaCategoriaFlores
        Datos[Index].CategoriaDiseno = NuevaCategoriaDiseno
        Datos[Index].CategoriaEventos = NuevaCategoriaEventos
        Datos[Index].Descripcion = NuevaDescripcion
        notifyItemChanged(Index)
    }

    fun EliminarDatos(nombreProducto: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- creo una variable que contenga
            //un PrepareStatement
            val DeleteUser = objConexion?.prepareStatement("DELETE TbInventario WHERE UUID_Producto = ?")!!
            DeleteUser.setString(1, nombreProducto)
            DeleteUser.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun ActualizarDato(
        NombreProducto: String,
        PrecioProducto: Float,
        CantidadProductoBodega: Int,
        CategoriaFlores: String,
        CategoriaDiseno: String,
        CategoriaEventos: String,
        DescripcionProducto: String,
        uuid: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {
            val ObjConexion = ClaseConexion().CadenaConexion()

            val UpdateProducto = ObjConexion?.prepareStatement(
                "UPDATE TbInventario SET Nombre_Producto = ?, Precio_Producto = ?, Cantidad_Bodega_Productos  = ?, Categoria_Producto = ?, Descripcion_Producto  = ? WHERE UUID_Producto = ?"
            )
            UpdateProducto?.apply {
                setString(1, NombreProducto)
                setFloat(2, PrecioProducto)
                setInt(3, CantidadProductoBodega)
                setString(4, CategoriaFlores)
                setString(5, CategoriaDiseno)
                setString(6, CategoriaEventos)
                setString(7, DescripcionProducto)
                setString(8, uuid)
                executeUpdate()
            }

            withContext(Dispatchers.Main){
                ActualizarListaDespuesDeEditar(uuid, NombreProducto, PrecioProducto, CantidadProductoBodega, CategoriaFlores, CategoriaDiseno, CategoriaEventos, DescripcionProducto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Inventory {
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_inventory, parent, false)
        return ViewHolder_Inventory(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Inventory, position: Int) {
        //Poder darle clic a la elemento de la card
        val item = Datos[position]
        holder.Nombre_Producto.text = item.Nombre
        holder.Precio_Producto.text = item.Precio.toString()

        Glide.with(holder.IMG_Producto_View.context)
            .load(item.IMG_Product)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Producto_View)

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
                EliminarDatos(item.uuid, position)
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

            val dialogView = LayoutInflater.from(context).inflate(R.layout.productos_actualizar, null)

            val Update_Nombre = dialogView.findViewById<EditText>(R.id.Txt_Nombre_Product)
            val Update_Precio = dialogView.findViewById<EditText>(R.id.Txt_Precio_Product)
            val Update_CantidadBodega = dialogView.findViewById<EditText>(R.id.Txt_CantidadBode_Product)
            val Update_Descripcion = dialogView.findViewById<EditText>(R.id.Txt_Descripcion_Product)


            Update_Nombre.filters = arrayOf(InputFilter.LengthFilter(10))
            Update_Precio.filters = arrayOf(InputFilter.LengthFilter(4))
            Update_Descripcion.filters = arrayOf(InputFilter.LengthFilter(35))

            Update_Nombre.setText(item.Nombre)
            Update_Precio.setText(item.Precio.toString())
            Update_CantidadBodega.setText(item.CantidadBode)
            Update_Descripcion.setText(item.Descripcion)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Usuario")
            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar") { dialog, _ ->
                val nombreProducto = Update_Nombre.text.toString()
                val precioProducto = Update_Precio.text.toString().toFloat()
                val cantidadProductoBodega = Update_CantidadBodega.text.toString().toInt()
                val descripcionProducto = Update_Descripcion.text.toString()

                ActualizarDato(nombreProducto, precioProducto, cantidadProductoBodega, item.CategoriaFlores, item.CategoriaDiseno, item.CategoriaEventos, descripcionProducto, item.uuid, GlobalScope)

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
                putString("img", item.IMG_Product)
                putString("nombre", item.Nombre)
                putString("precio", item.Precio.toString())
                putString("cantidadBodega", item.CantidadBode.toString())
                putString("categoria", item.CategoriaFlores)
                putString("descripcion", item.Descripcion)
            }

            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_Users, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}