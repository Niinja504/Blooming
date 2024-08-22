package RecyclerViewHelpers

import DataC.DataOffers_Admin
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import proyecto.expotecnica.blooming.R

class Adaptador_Offers_Client (var Datos: List<DataOffers_Admin>): RecyclerView.Adapter<ViewHolder_Offers_Client>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Offers_Client{
        val Vista = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_offer_admin, parent, false)
        return  ViewHolder_Offers_Client(Vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder_Offers_Client, posicion: Int){
        val item = Datos[posicion]
        holder.CampoTitulo.text = item.Titulo
        holder.CampoPorcentaje.text = item.Porcentaje

        Glide.with(holder.IMG_Archivo.context)
            .load(item.Img_oferta)
            .placeholder(R.drawable.profile_user)
            .error(R.drawable.profile_user)
            .into(holder.IMG_Archivo)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("titulo", item.Titulo)
                putString("porcentaje", item.Porcentaje)
                putString("descripcion", item.Descripcion)
                putString("img", item.Img_oferta)
            }

            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.navigation_Details_Offers, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}