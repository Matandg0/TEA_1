package com.example.data.sampledata

import android.app.Application
import android.util.Log
import androidx.room.Room.databaseBuilder
import com.example.data.sampledata.database.AppDataBaseList
import com.example.data.sampledata.database.AppDataBasePost
import fr.ec.app.data.api.response.PostResponse
import fr.ec.app.data.database.entities.ListEntity
import fr.ec.app.data.database.entities.PostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DatabaseProvider(private val application: Application) {


    private val appDatabasePost: AppDataBasePost =
        databaseBuilder(application, AppDataBasePost::class.java, "app-database-post").build()

    private val appDatabaseList: AppDataBaseList =
        databaseBuilder(application, AppDataBaseList::class.java, "app-database-list").build()




    suspend fun savePosts(list : List<PostResponse>, listId : Int) = withContext(Dispatchers.Default) {
        try {

            appDatabasePost.postDao().add(list.map {
                PostEntity(
                    id = it.id.toInt(),
                    label = it.label,
                    listId = listId
                )
            })


        } catch (e: Exception) {
            Log.e("DatabaseProvider", "error saving database: ${e.message}")
        }
    }

    suspend fun saveLists(list : List<PostResponse>) = withContext(Dispatchers.Default) {
        try {

            appDatabaseList.postDaoList().add(list.map {
                ListEntity(
                    id = it.id.toInt(),
                    label = it.label
                )
            })


        } catch (e: Exception) {
            Log.e("DatabaseProvider", "error saving database: ${e.message}")
        }
    }

    suspend fun getPosts(listId : Int) : List<PostResponse> = withContext(Dispatchers.Default) {
        val savedPosts = appDatabasePost.postDao().getPosts(listId.toString())

        val postList =
            savedPosts.map {
                PostResponse(
                    id = it.id.toString(),
                    label = it.label
                )
            }
        postList
    }

    suspend fun getLists() : List<PostResponse> = withContext(Dispatchers.Default) {
        val savedPosts = appDatabaseList.postDaoList().getLists()

        val postList =
            savedPosts.map {
                PostResponse(
                    id = it.id.toString(),
                    label = it.label
                )
            }
        postList
    }





}