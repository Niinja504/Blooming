package Firebase_SingIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import proyecto.expotecnica.blooming.R

class NomUsuario : DialogFragment() {
    private lateinit var etUser: EditText
    private lateinit var btnSubmitUser: Button
    var onSubmitClickListener: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.nombre_usuario_fireb, container, false)
        etUser = view.findViewById(R.id.txt_Usuario_fireb)
        btnSubmitUser = view.findViewById(R.id.btn_sig_Usuario_Fireb)
        btnSubmitUser.setOnClickListener {
            val username = etUser.text.toString()
            if (username.isNotEmpty()) {
                onSubmitClickListener?.invoke(username)
                dismiss()
            } else {
                Toast.makeText(context, "Por favor, ingrese un nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}
