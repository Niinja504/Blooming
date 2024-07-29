package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import proyecto.expotecnica.blooming.R

class ViewHolder_Users (view: View): RecyclerView.ViewHolder(view) {
    val IMG_User_View: ImageView = view.findViewById(R.id.IMG_Users_CardAdmin)
    val Nombre_Usuario: TextView = view.findViewById(R.id.lbl_NombreUsuario_CardAdmin)
    val NomberDeUsuario: TextView = view.findViewById(R.id.lbl_NombreDeUsu_CardAdmin)
    val RolUsuario: TextView = view.findViewById(R.id.lbl_Rol_CardAdmin)
    val IC_Editar: ImageView = view.findViewById(R.id.IC_Edit_Users_CardAdmin)
    val IC_Eliminar: ImageView = view.findViewById(R.id.IC_Delete_Users_CardAdmin)
}