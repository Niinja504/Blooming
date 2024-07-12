package proyecto.expotecnica.blooming.Employed.orders_delivered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrdersDeliveredViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is orders delivered Fragment"
    }
    val text: LiveData<String> = _text
}