package proyecto.expotecnica.blooming.Client.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class Setting : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_setting_client, container, false)

        //Variables que se van a utilizar
        val Ic_Regresar = root.findViewById<ImageView>(R.id.Regresar_setting_client)

        val IG = root.findViewById<ImageView>(R.id.IC_IG_Setting_Client)
        val X = root.findViewById<ImageView>(R.id.IC_X_Setting_Client)
        val TikTok = root.findViewById<ImageView>(R.id.IC_TikTok_Setting_Client)

        Ic_Regresar.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile_client)
        }

        IG.setOnClickListener {
            openUrl("https://www.instagram.com/_sistema_blooming?igsh=aWRtOWZ4cHZsMnli")
        }

        X.setOnClickListener {
            openUrl("https://x.com/SistemaBlooming")
        }

        TikTok.setOnClickListener {
            openUrl("https://www.tiktok.com/@sistema_blooming?_t=8oRwbbrEw6g&_r=1")
        }

        return root
    }

    private fun openUrl(link: String){
        val uri = Uri.parse(link)
        val inte = Intent(Intent.ACTION_VIEW, uri)

        startActivity(inte)
    }
}