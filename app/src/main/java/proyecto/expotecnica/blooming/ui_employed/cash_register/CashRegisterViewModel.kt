package proyecto.expotecnica.blooming.ui_employed.cash_register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CashRegisterViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is cash register Fragment"
    }
    val text: LiveData<String> = _text
}