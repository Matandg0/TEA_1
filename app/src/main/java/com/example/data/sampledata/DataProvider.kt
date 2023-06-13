package fr.ec.app.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import fr.ec.app.data.api.response.HashResponse
import fr.ec.app.data.api.response.ItemsResponse
import fr.ec.app.data.api.response.PostResponse
import fr.ec.app.data.api.response.ListsResponse
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


object DataProvider {

    private val BACKGOURND = Executors.newFixedThreadPool(2)


    private val POST_API_URL =
        "http://tomnab.fr/todo-api/"


    private val gson = Gson()
    fun getData(context : Context, stringText: String, list : Boolean, onSuccess :(List<PostResponse>)->Unit,onError : (Throwable)->Unit)  {
        BACKGOURND.submit {
            try {
                val json :String? = makeCall(context, stringText)
                Log.e("DataProvider","reponse json : $json")
                if (list){
                    val listsResponse = gson.fromJson(json, ListsResponse::class.java)

                    val postList = listsResponse.lists.map {
                        PostResponse(
                            id = it.id,
                            label = it.label
                        )
                    }
                    Log.e("DataProvider","reponse final : $listsResponse")
                    onSuccess(postList)
                } else {
                    val listsResponse = gson.fromJson(json, ItemsResponse::class.java)

                    val postList = listsResponse.items.map {
                        PostResponse(
                            id = it.id,
                            label = it.label
                        )
                    }
                    Log.e("DataProvider","reponse final : $listsResponse")
                    onSuccess(postList)
                }


            }catch (e :Exception) {
                onError(e)
            }
        }
    }

    fun getHash(context : Context, user: String, password: String,onSuccess :(String)->Unit,onError : (Throwable)->Unit)  {
        BACKGOURND.submit {
            try {
                val json :String? = makeAuthentication(context,user,password)
                val HashResponse = gson.fromJson<HashResponse>(json, HashResponse::class.java)
                val hash = HashResponse.hash

                if (hash != null) {
                    onSuccess(hash)
                }

            }catch (e :Exception) {
                onError(e)
            }
        }
    }

    private fun makeAuthentication(context : Context, user : String, password : String): String? {
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        val sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        // Obtient la valeur actuelle de POST_API_URL à partir des préférences partagées
        var currentPostApiUrl = sharedPreferences.getString("post_api_url", POST_API_URL)
        if (currentPostApiUrl.isNullOrEmpty()){
            currentPostApiUrl = POST_API_URL
        }

        try {
            urlConnection = URL(currentPostApiUrl + "authenticate?user=" + user + "&password=" + password).openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.connect()

            reader = urlConnection.inputStream?.bufferedReader()
            return reader?.readText()

        } finally {
            urlConnection?.disconnect()
            reader?.close()
        }
    }


    private fun makeCall(context : Context, stringText : String): String? {
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        val sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        // Obtient la valeur actuelle de POST_API_URL à partir des préférences partagées
        var currentPostApiUrl = sharedPreferences.getString("post_api_url", POST_API_URL)
        if (currentPostApiUrl.isNullOrEmpty()){
            currentPostApiUrl = POST_API_URL
        }

        var token = sharedPreferences.getString("token","")

        try {
            var url = currentPostApiUrl + stringText + "?hash=" + token
            Log.e("DataProvider","API Call for URL : $url")
            urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            reader = urlConnection.inputStream?.bufferedReader()
            return reader?.readText()

        } finally {
            urlConnection?.disconnect()
            reader?.close()
        }
    }

}