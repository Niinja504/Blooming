package proyecto.expotecnica.blooming.Client

import DataC.ProductData_Client
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel_Product_Client : ViewModel() {
    private val _productList = MutableLiveData<MutableList<ProductData_Client>>(mutableListOf())
    val productList: LiveData<MutableList<ProductData_Client>> get() = _productList

    private val _descuentos = MutableLiveData<Map<String, Int>>(emptyMap())
    val descuentos: LiveData<Map<String, Int>> get() = _descuentos

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun addProduct(productData: ProductData_Client) {
        val list = _productList.value ?: mutableListOf()
        if (list.any { it.uuid == productData.uuid }) {
            _toastMessage.value = "Ya está registrado"
        } else {
            list.add(productData)
            _productList.value = list
            _toastMessage.value = "Se ha añadido al pedido"
        }
    }

    fun EliminarProducto(productUuid: String) {
        val currentList = _productList.value?.toMutableList() ?: mutableListOf()
        val productToRemove = currentList.find { it.uuid == productUuid }
        if (productToRemove != null) {
            currentList.remove(productToRemove)
            _productList.value = currentList
            _toastMessage.value = "Producto eliminado"
        } else {
            _toastMessage.value = "Producto no encontrado"
        }
    }

    fun LimpiarListaProductos() {
        _productList.value?.clear()
        _productList.value = mutableListOf()
    }

    fun setDescuentos(descuentos: Map<String, Int>) {
        _descuentos.value = descuentos
    }
}
