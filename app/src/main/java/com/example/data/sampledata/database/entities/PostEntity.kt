package fr.ec.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Contient tous les posts de la 2e pages
// Le lien avec les listes de la 1ere page est fait avec listId
@Entity
data class PostEntity(
    @PrimaryKey
    val id : Int,
    val label : String,
    val listId : Int
)


@Entity
data class ListEntity(
    @PrimaryKey
    val id : Int,
    val label : String
)