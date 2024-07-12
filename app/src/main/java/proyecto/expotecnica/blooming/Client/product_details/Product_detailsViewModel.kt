package proyecto.expotecnica.blooming.Client.product_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Product_detailsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is product details Fragment"
    }
    val text: LiveData<String> = _text
}