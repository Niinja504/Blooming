package DataC

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data_Productos(
    val nombre: String,
    val imageUrl: String,
    val cantidad: Int,
    val precio: Float
) : Parcelable
