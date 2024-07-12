package proyecto.expotecnica.blooming.Client.delivery_date

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeliveryDateViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is delivery date Fragment"
    }
    val text: LiveData<String> = _text
}