package com.example.data.sampledata.database

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.ec.app.data.database.dao.PostDao
import fr.ec.app.data.database.dao.PostDaoList
import fr.ec.app.data.database.entities.ListEntity
import fr.ec.app.data.database.entities.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class AppDataBasePost : RoomDatabase() {
    abstract fun postDao() : PostDao
}

@Database(entities = [ListEntity::class], version = 1)
abstract class AppDataBaseList : RoomDatabase() {
    abstract fun postDaoList() : PostDaoList
}