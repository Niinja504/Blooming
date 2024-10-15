package proyecto.expotecnica.blooming.Admin.social_media

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class Social : Fragment() {
    private var UUID: String? = null
    private lateinit var fragmentLayout: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            UUID = it.getString("UUID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_setting_admin, container, false)

        //Variables que se van a utilizar
        val Ic_Regresar = root.findViewById<ImageView>(R.id.Regresar_setting_admin)

        val IG = root.findViewById<ImageView>(R.id.IC_IG_Setting_Admin)
        val X = root.findViewById<ImageView>(R.id.IC_X_Setting_Admin)
        val TikTok = root.findViewById<ImageView>(R.id.IC_TikTok_Setting_Admin)

        Ic_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile_admin)
        }

        IG.setOnClickListener {
            openUrl("https://www.instagram.com/ptc_blooming/profilecard/?igsh=MWhoaXdwMTF5cnBndw==")
        }

        X.setOnClickListener {
            openUrl("https://x.com/bloomingptc?s=21&t=13sdLei3-0u_F-QRr6TXlg")
        }

        TikTok.setOnClickListener {
            openUrl("https://www.tiktok.com/@bloomingptc?_t=8qY14Ab1G57&_r=1")
        }

        return root
    }

    private fun openUrl(link: String){
        val uri = Uri.parse(link)
        val inte = Intent(Intent.ACTION_VIEW, uri)

        startActivity(inte)
    }
}