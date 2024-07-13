package proyecto.expotecnica.blooming.Admin.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmployedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is employed Fragment"
    }
    val text: LiveData<String> = _text
}