package proyecto.expotecnica.blooming.Client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import kotlin.concurrent.schedule

class ImageViewModel_Client : ViewModel() {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    private val _uuid = MutableLiveData<String?>()
    val uuid: LiveData<String?> get() = _uuid

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> get() = _email

    private var timer: Timer? = null

    init {
        startImageUrlUpdates()
    }

    private fun startImageUrlUpdates() {
        timer = Timer()
        timer?.schedule(0, 5000) {
            val newUrl = fetchNewImageUrl()
            setImageUrl(newUrl)
        }
    }

    fun setImageUrl(url: String?) {
        _imageUrl.postValue(url)
    }

    fun setUuid(uuid: String?) {
        _uuid.value = uuid
    }

    fun setEmail(email: String?) {
        _email.value = email
    }

    private fun fetchNewImageUrl(): String? {
        return _imageUrl.value // Aquí debes cambiarlo para obtener una nueva URL
    }
}