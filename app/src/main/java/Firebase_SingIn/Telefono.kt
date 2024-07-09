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

class Telefono : DialogFragment() {
    private lateinit var etPhone: EditText
    private lateinit var btnSubmitPhone: Button
    var onSubmitClickListener: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.telefono_usuario_fireb, container, false)
        etPhone = view.findViewById(R.id.txt_Telef_usu_Fireb)
        btnSubmitPhone = view.findViewById(R.id.btn_Sig_Telefono_Fireb)
        btnSubmitPhone.setOnClickListener {
            val phone = etPhone.text.toString()
            if (phone.isNotEmpty()) {
                onSubmitClickListener?.invoke(phone)
                dismiss()
            } else {
                Toast.makeText(context, "Por favor, ingrese un número de teléfono", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}