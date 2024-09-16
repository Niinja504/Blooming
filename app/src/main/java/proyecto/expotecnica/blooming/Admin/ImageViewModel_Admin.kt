package proyecto.expotecnica.blooming.Admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel_Admin : ViewModel() {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    private val _uuid = MutableLiveData<String?>()
    val uuid: LiveData<String?> get() = _uuid

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> get() = _email

    fun setImageUrl(url: String?) {
        _imageUrl.value = url
    }

    fun setUuid(uuid: String?) {
        _uuid.value = uuid
    }

    fun setEmail(email: String?) {
        _email.value = email
    }
}
