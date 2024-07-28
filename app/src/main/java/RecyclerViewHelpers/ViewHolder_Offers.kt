package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Offers (view: View): RecyclerView.ViewHolder(view) {
    val CampoTitulo: TextView = view.findViewById(R.id.lbl_Titulo_CardOffers)
    val IMG_Archivo: ImageView = view.findViewById(R.id.IMG_Archivo_CardOffers)
    val IC_Editar: ImageView = view.findViewById(R.id.IC_Edit_CardOffer)
    val IC_Delete: ImageView = view.findViewById(R.id.IC_Edit_CardOffer)
}