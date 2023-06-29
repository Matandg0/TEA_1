package fr.ec.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ec.app.data.database.entities.ListEntity
import fr.ec.app.data.database.entities.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE )
    fun add(posts: List<PostEntity>)

    @Query("SELECT * FROM PostEntity WHERE listId = :listId")
    suspend fun getPosts(listId: String): List<PostEntity>
}

@Dao
interface PostDaoList {
    @Insert(onConflict = OnConflictStrategy.REPLACE )
    fun add(lists: List<ListEntity>)

    @Query("SELECT * FROM ListEntity")
    suspend fun getLists() : List<ListEntity>
}