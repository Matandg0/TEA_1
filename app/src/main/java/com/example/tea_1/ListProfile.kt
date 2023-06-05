package com.example.tea_1

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ListProfile {
    private const val KEY_PREFIX = "list_"

    data class ListItem(val Nom: String, val Checked: Boolean)
    data class ListData(val Nom: String, val items: List<ListItem>?)
    data class Item(val id: Int, val name: String)


    fun saveList(context: Context, pseudo : String?, listData: List<ListData>?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val json = Gson().toJson(listData)
        sharedPreferences.edit().putString(KEY_PREFIX + pseudo, json).apply()
    }

    fun loadList(context: Context, pseudo : String?): List<ListData>? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val json = sharedPreferences.getString(KEY_PREFIX + pseudo, null)
        return if (json != null) {
            val typeToken = object : TypeToken<List<ListData>>() {}.type
            Gson().fromJson(json, typeToken)
        } else {
            emptyList()
        }
    }

    fun addEltToList(context : Context, pseudo : String?, name : String) {
        val list = loadList(context, pseudo)?.toMutableList() // Convertir en liste mutable
        val newElt = ListData(name, null)
        list?.add(newElt) // Ajouter le nouvel élément à la liste mutable

        saveList(context, pseudo, list) // Enregistrer la liste mise à jour dans les préférences

    }
}

