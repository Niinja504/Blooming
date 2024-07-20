package proyecto.expotecnica.blooming.Admin.change_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R

class ChangePassword : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_change_password, container, false)

        val Regresar_ChangePassword = root.findViewById<ImageView>(R.id.IC_Regresar_ChangePassword)

        Regresar_ChangePassword.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile_admin)
        }


        return root
    }
}