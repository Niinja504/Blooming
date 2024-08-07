package proyecto.expotecnica.blooming.Admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel_Admin : ViewModel() {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    fun setImageUrl(url: String?) {
        _imageUrl.value = url
    }
}