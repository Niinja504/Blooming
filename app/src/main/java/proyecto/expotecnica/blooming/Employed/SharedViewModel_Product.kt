package proyecto.expotecnica.blooming.Employed

import DataC.ProductData_Employed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel_Product : ViewModel() {
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
}
