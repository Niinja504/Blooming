package modelo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class MostrarIMG(private val claseConexion: ClaseConexion) {
    suspend fun obtenerImagenUsuario(uuid: String): String? {
        val sql = "SELECT Img_User FROM TbUsers WHERE UUID_User = ?"
        var urlImagen: String? = null

        withContext(Dispatchers.IO) {
            val conexion: Connection? = claseConexion.CadenaConexion()
            try {
                if (conexion != null) {
                    val statement: PreparedStatement = conexion.prepareStatement(sql)
                    statement.setString(1, uuid)

                    val resultado: ResultSet = statement.executeQuery()

                    if (resultado.next()) {
                        urlImagen = resultado.getString("Img_User")
                    } else {
                        Log.e("MostrarIMG", "No se encontró imagen para el UUID: $uuid")
                    }
                } else {
                    Log.e("MostrarIMG", "No se pudo establecer la conexión a la base de datos.")
                }
            } catch (e: Exception) {
                Log.e("MostrarIMG", "Error al ejecutar la consulta SQL: $e")
            } finally {
                try {
                    conexion?.close()
                } catch (e: Exception) {
                    Log.e("MostrarIMG", "Error al cerrar la conexión: $e")
                }
            }
        }
        //retornamos en la direcciòn de la imagen
        return urlImagen
    }
}