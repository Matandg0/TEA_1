package com.example.tea_1

import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.core.content.ContentProviderCompat.requireContext
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceManager
import android.preference.Preference.OnPreferenceClickListener

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Charge les préférences à partir du fichier XML
        addPreferencesFromResource(R.xml.preferences)

        // Obtient l'instance des préférences partagées
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Récupère la préférence pour la valeur de POST_API_URL
        val postApiUrlPreference = findPreference("post_api_url") as EditTextPreference

        // Obtient la valeur actuelle de POST_API_URL à partir des préférences partagées
        val currentPostApiUrl = sharedPreferences.getString("post_api_url", "")

        // Affiche la valeur actuelle de POST_API_URL dans la préférence
        postApiUrlPreference.text = currentPostApiUrl

        // Définit l'OnClickListener pour la préférence "post_api_url"
        postApiUrlPreference.setOnPreferenceChangeListener { preference, newValue ->
            // Enregistre la nouvelle valeur de POST_API_URL dans les préférences partagées
            val editor = sharedPreferences.edit()
            editor.putString("post_api_url", newValue as String)
            editor.apply()
            true
        }

        val clearPseudoHistoryPreference = findPreference("clear_pseudo_history")

        // Définit l'OnClickListener pour la préférence "Vider l'historique des pseudos"
        clearPseudoHistoryPreference.setOnPreferenceClickListener {
            // Supprime l'historique des pseudos des préférences partagées
            val editor = sharedPreferences.edit()
            editor.remove("pseudo_history")
            editor.apply()
            true
        }
    }
}

