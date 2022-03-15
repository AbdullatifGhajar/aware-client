@file:JvmName("ServerInterface")
@file:JvmMultifileClass

package com.aware.phone

// import com.aware.phone.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ServerException(message: String) : Exception(message)

class ServerInterface {
    companion object {
        private const val key = "hef3TF%5eVg90546bvgFVL>Zzxskfou;aswperwrsf,c/x"
        private const val serverUrlPath = "https://hpi.de/baudisch/projects/neo4j/api"

        private fun getFromServer(route: String, arguments: String = ""): JSONObject {
            var responseBody = ""
            runBlocking {
                async(Dispatchers.IO) {
                    val connection =
                        URL("$serverUrlPath$route?key=$key&$arguments").openConnection() as HttpURLConnection
                    connection.disconnect()

                    if (connection.responseCode != HttpURLConnection.HTTP_OK)
                        throw ServerException("GET response code ${connection.responseCode}")
                    responseBody = connection.inputStream.reader().readText()

                }.join()
            }
            return JSONObject(responseBody)
        }

        fun authenticateUser(email: String, password: String): Boolean {
            return try {
                getFromServer(
                    "/users",
                    "email=$email&password=$password"
                )
                true
            } catch (e: ServerException) {
                false
            }
        }

        fun getBalance(email: String, password: String): String {
            return try {
                getFromServer(
                    "/balance",
                    "email=$email&password=$password"
                ).getString("balance")
            } catch (e: ServerException) {
                "error"
            }
        }
    }
}