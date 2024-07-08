package modelo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    @Throws(IOException::class)
    fun resizeImageIfNeeded(imageBitmap: Bitmap): Bitmap {
        val imageHeight = imageBitmap.height
        val imageWidth = imageBitmap.width

        if (imageHeight > 1080 || imageWidth > 1080) {
            val scaledHeight: Int
            val scaledWidth: Int
            if (imageHeight > imageWidth) {
                scaledHeight = 1080
                scaledWidth = (imageWidth * (1080.0 / imageHeight)).toInt()
            } else {
                scaledWidth = 1080
                scaledHeight = (imageHeight * (1080.0 / imageWidth)).toInt()
            }
            return Bitmap.createScaledBitmap(imageBitmap, scaledWidth, scaledHeight, true)
        }
        return imageBitmap
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(null)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
}
