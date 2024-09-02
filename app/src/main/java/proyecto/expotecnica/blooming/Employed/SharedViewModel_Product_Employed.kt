package proyecto.expotecnica.blooming.Employed

import DataC.ProductData_Employed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel_Product_Employed : ViewModel() {
    private val _productList = MutableLiveData<MutableList<ProductData_Employed>>(mutableListOf())
    val productList: LiveData<MutableList<ProductData_Employed>> get() = _productList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun addProduct(productData: ProductData_Employed) {
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
}
