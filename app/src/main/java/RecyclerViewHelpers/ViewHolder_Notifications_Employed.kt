package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Notifications_Employed (view: View): RecyclerView.ViewHolder(view) {
    val Titulo: TextView = view.findViewById(R.id.lbl_Titulo_Notification_CardEmployed)
    val Mensaje: TextView = view.findViewById(R.id.lbl_Mensaje_Notification_CardEmployed)
    val Tiempo: TextView = view.findViewById(R.id.lbl_HoraEnvio_Notification_CardEmployed)
    val Fecha: TextView = view.findViewById(R.id.lbl_FechaEnvio_Notification_CardEmployed)
    val IC_Eliminar: ImageView = view.findViewById(R.id.Ic_Delete_Notification_CardEmployed)
}