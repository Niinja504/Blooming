package modelo

import java.sql.Connection
import java.sql.DriverManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClaseConexion {
    suspend fun CadenaConexion(): Connection? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "jdbc:oracle:thin:@10.2.0.2:1521:xe"
                val usuario = "BLOOM"
                val contrasena = "BloX5558"
                DriverManager.getConnection(url, usuario, contrasena)
            } catch (e: Exception) {
                println("Error en la cadena de conexión: $e")
                null
            }
        }
    }
}