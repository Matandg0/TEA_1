package com.example.tea_1

import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.Preference.OnPreferenceClickListener

class SettingsActivity : PreferenceActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Charge les préférences à partir du fichier XML
        addPreferencesFromResource(R.xml.preferences)

        // Obtient l'instance des préférences partagées
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

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
