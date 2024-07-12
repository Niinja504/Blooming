package proyecto.expotecnica.blooming.Employed.product_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductDetailsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is product details Fragment"
    }
    val text: LiveData<String> = _text
}