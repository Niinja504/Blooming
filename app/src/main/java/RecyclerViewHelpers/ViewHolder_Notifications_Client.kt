package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Notifications_Client (view: View): RecyclerView.ViewHolder(view) {
    val Titulo: TextView = view.findViewById(R.id.lbl_Titulo_Notification_CardClient)
    val Mensaje: TextView = view.findViewById(R.id.lbl_Mensaje_Notification_CardClient)
    val Tiempo: TextView = view.findViewById(R.id.lbl_HoraEnvio_Notification_CardClient)
    val Fecha: TextView = view.findViewById(R.id.lbl_FechaEnvio_Notification_CardClient)
    val IC_Eliminar: ImageView = view.findViewById(R.id.Ic_Delete_Notification_CardClient)
}