package proyecto.expotecnica.blooming.ui_client.order_confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderConfirmationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is order confirmation Fragment"
    }
    val text: LiveData<String> = _text
}