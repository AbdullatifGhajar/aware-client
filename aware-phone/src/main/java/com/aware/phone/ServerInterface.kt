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

        /* fun getAuthenticatedUser(email: String, password: String): LoggedInUser {
            val responseJson = getFromServer(
                "/users",
                "email=$email&password=$password"
            )

            return LoggedInUser(
                responseJson.getString("id"),
                responseJson.getString("email"),
                responseJson.getString("displayName")
            )
        } */
    }
}