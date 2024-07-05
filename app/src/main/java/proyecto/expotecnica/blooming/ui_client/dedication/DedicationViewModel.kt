package proyecto.expotecnica.blooming.ui_client.dedication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DedicationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is dedication Fragment"
    }
    val text: LiveData<String> = _text
}