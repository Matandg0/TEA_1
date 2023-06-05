package com.example.tea_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.preference.PreferenceManager


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //--------Afficher Pseudo par défaut---------------
        // Obtient l'instance des préférences partagées
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Affiche le dernier pseudo de l'historique s'il n'est pas vide, sinon affiche le pseudo par défaut des préférences
        val pseudoSet = sharedPreferences.getStringSet("pseudo_history", HashSet())
        val lastPseudo = pseudoSet?.lastOrNull() ?: sharedPreferences.getString("default_pseudo", "")

        val pseudoTextView = findViewById<TextView>(R.id.pseudo_input)
        pseudoTextView.text = lastPseudo



        val okButton = findViewById<Button>(R.id.button)

        // Définit l'OnClickListener pour le bouton OK
        okButton.setOnClickListener {
            // Récupère le pseudo entré par l'utilisateur
            val pseudo = pseudoTextView.text.toString()

            // Sauvegarde l'historique des pseudos dans les préférences partagées
            pseudoSet?.add(pseudo)
            val editor = sharedPreferences.edit()
            editor.putStringSet("pseudo_history", pseudoSet)
            editor.apply()

            //Ouverture d'une nouvelle activité
            val intent = Intent(this, ChoixListActivity::class.java)
            intent.putExtra("pseudo", pseudo)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}




