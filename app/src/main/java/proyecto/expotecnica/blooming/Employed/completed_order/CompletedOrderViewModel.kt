package proyecto.expotecnica.blooming.Employed.completed_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompletedOrderViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is completed order Fragment"
    }
    val text: LiveData<String> = _text
}