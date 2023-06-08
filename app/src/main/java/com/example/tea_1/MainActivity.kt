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
import fr.ec.app.data.DataProvider
import fr.ec.app.data.Post

private var apiConnected = false
private lateinit var okButton: Button


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
        val lastPseudo =
            pseudoSet?.lastOrNull() ?: sharedPreferences.getString("default_pseudo", "")

        val pseudoTextView = findViewById<TextView>(R.id.pseudo_input)
        pseudoTextView.text = lastPseudo

        DataProvider.getHash(
            onSuccess = { hash ->
                // Connexion réussie
                this@MainActivity.runOnUiThread {
                    handleApiResponse(hash)
                }
            },
            onError = { error ->
                // Erreur de connexion
                this@MainActivity.runOnUiThread {
                    // Mettez ici le code à exécuter lorsque la connexion a échoué
                    // Gérez l'erreur
                    handleApiError(error)
                }
            }
        )



        okButton = findViewById<Button>(R.id.button)
        // Définit l'OnClickListener pour le bouton OK
        okButton.setOnClickListener {
            // Récupère le pseudo entré par l'utilisateur
            val pseudo = pseudoTextView.text.toString()


            // Vérifie si un pseudo a été renseigné et si la connexion à l'API est valide
            if (pseudo.isNotEmpty() && apiConnected) {

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
    }

    private fun handleApiResponse(hash : String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putString("token", hash)
        Log.e("MainActivity","API Connected")
        // Gérer la réponse de l'API
        apiConnected = true

        // Activer le bouton "OK"
        okButton.isEnabled = true
    }

    private fun handleApiError(error: Throwable) {
        // Gérer l'erreur de l'API
        apiConnected = false

        // Désactiver le bouton "OK"
        okButton.isEnabled = false

        // Afficher un message d'erreur ou effectuer d'autres actions appropriées
        Log.e("MainActivity", "Erreur de connexion à l'API: ${error.message}")
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




