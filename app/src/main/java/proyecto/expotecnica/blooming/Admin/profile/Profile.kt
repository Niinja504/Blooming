package proyecto.expotecnica.blooming.Admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import proyecto.expotecnica.blooming.R
import proyecto.expotecnica.blooming.databinding.FragmentProfileAdminBinding

class Profile : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_profile_admin, container, false)

        val ChangePassword = root.findViewById<ImageView>(R.id.IC_ChangePassword_Admin)

        ChangePassword.setOnClickListener{
            findNavController().navigate(R.id.action_ChangePassword)
        }


        return root
    }
}