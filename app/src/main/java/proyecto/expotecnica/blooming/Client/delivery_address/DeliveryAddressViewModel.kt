package proyecto.expotecnica.blooming.Client.delivery_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeliveryAddressViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is delivery address Fragment"
    }
    val text: LiveData<String> = _text
}