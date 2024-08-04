package proyecto.expotecnica.blooming.Employed.cash_register

import DataC.DataInventory_Admin
import DataC.DataInventory_Employed
import RecyclerViewHelpers.Adaptador_CashRegister_Employed
import RecyclerViewHelpers.Adaptador_Inventory_Admin
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.databinding.FragmentCashRegisterEmployedBinding

class CashRegister : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_cash_register_employed, container, false)

        val RCV_Cash = root.findViewById<RecyclerView>(R.id.RCV_CashRegister_Employed)
        //Asignarle un Layout al RecyclerView
        RCV_Cash.layoutManager = LinearLayoutManager(requireContext())

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        RCV_Cash.layoutManager = gridLayoutManager

        suspend fun MostrarDatos(): List<DataInventory_Employed> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val ResultSet = statement?.executeQuery("SELECT * FROM TbInventario")!!

            //Voy a guardar all lo que me traiga el Select

            val Productos = mutableListOf<DataInventory_Employed>()

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
                val Producto = DataInventory_Employed(uuid, IMG_Produc, Nombre, Precio, CantidadBode, CategoriaFlores, CategoriaDiseno, CategoriaEvento, Descripcion)
                Productos.add(Producto)
            }
            return Productos
        }

        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val ProductosDB = MostrarDatos()
            withContext(Dispatchers.Main){
                val miAdaptador = Adaptador_CashRegister_Employed(ProductosDB)
                RCV_Cash.adapter = miAdaptador
            }
        }

        return root
    }
}