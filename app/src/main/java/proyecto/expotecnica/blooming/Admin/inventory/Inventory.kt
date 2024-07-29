package proyecto.expotecnica.blooming.Admin.inventory

import DataC.DataInventory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import modelo.ClaseConexion
import RecyclerViewHelpers.Adaptador_Inventory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proyecto.expotecnica.blooming.R

class Inventory : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_inventory_admin, container, false)

        //Variables que se van a utilizar
        val AgregarProducto = root.findViewById<Button>(R.id.btn_AgregarProducto_Inventory)

        val RCV_Inventory = root.findViewById<RecyclerView>(R.id.RCV_Inventory_Admin)
        //Asignarle un Layout al RecyclerView
        RCV_Inventory.layoutManager = LinearLayoutManager(requireContext())


        AgregarProducto.setOnClickListener{
            findNavController().navigate(R.id.action_AddProduct_admin)
        }

        suspend fun MostrarDatos(): List<DataInventory> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val ResultSet = statement?.executeQuery("SELECT * FROM TbInventario")!!

            //Voy a guardar all lo que me traiga el Select

            val Productos = mutableListOf<DataInventory>()

            while (ResultSet.next()){
                val IMG_Produc = ResultSet.getString("Img_Producto")
                val Nombre = ResultSet.getString("Nombre_Producto")
                val Precio = ResultSet.getFloat("Precio_Producto")
                val CantidadBode = ResultSet.getInt("Cantidad_Bodega_Productos")
                val CategoriaFlores = ResultSet.getString("Categoria_Flores")
                val CategoriaDiseno = ResultSet.getString("Categoria_Diseno")
                val CategoriaEvento = ResultSet.getString("Categoria_Evento")
                val Descripcion = ResultSet.getString("Descripcion_Producto")
                val uuid = ResultSet.getString("UUID_Producto")
                val Producto = DataInventory(uuid, IMG_Produc, Nombre, Precio, CantidadBode, CategoriaFlores, CategoriaDiseno, CategoriaEvento, Descripcion)
                Productos.add(Producto)
            }
            return Productos
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val ProductosDB = MostrarDatos()
            withContext(Dispatchers.Main){
                val miAdaptador = Adaptador_Inventory(ProductosDB)
                RCV_Inventory.adapter = miAdaptador
            }
        }

        return root
    }
}

