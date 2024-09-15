package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Offers_Client (view: View): RecyclerView.ViewHolder(view)  {
    val CampoTitulo: TextView = itemView.findViewById(R.id.lbl_Titulo_CardOffers_Client)
    val CampoPorcentaje: TextView = itemView.findViewById(R.id.lbl_Porcentaje_CardOffers_Client)
    val IMG_Archivo: ImageView = itemView.findViewById(R.id.IMG_Archivo_CardOffers_Client)
}