package modelo

import java.sql.Connection
import java.sql.DriverManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClaseConexion {
    suspend fun CadenaConexion(): Connection? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "jdbc:oracle:thin:@192.168.0.17:1521:xe"
                val usuario = "system"
                val contrasena = "hr2lnnuC"
                DriverManager.getConnection(url, usuario, contrasena)
            } catch (e: Exception) {
                println("Error en la cadena de conexión: $e")
                null
            }
        }
    }
}