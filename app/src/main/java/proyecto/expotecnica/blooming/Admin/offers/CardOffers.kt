package proyecto.expotecnica.blooming.Admin.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import proyecto.expotecnica.blooming.R

class CardOffers : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_card_offer_admin, container, false)

        return root
    }

}