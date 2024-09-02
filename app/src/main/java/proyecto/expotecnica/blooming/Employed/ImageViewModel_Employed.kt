package proyecto.expotecnica.blooming.Employed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel_Employed : ViewModel() {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    fun setImageUrl(url: String?) {
        _imageUrl.value = url
    }
}