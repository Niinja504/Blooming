package proyecto.expotecnica.blooming.Admin.add_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import proyecto.expotecnica.blooming.R

class AddProduct : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_add_product_inventory, container, false)

        //Variables que se van a utilizar


        return root
    }

    fun CheckBoxClick(view: View){
        if (view is MaterialCheckBox){
            val checked: Boolean = view.isChecked
            when(view.id){
                R.id.java_check_box{
                }
            }
        }
    }
}