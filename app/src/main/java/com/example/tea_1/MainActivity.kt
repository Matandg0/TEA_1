package com.example.tea_1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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

private var webConnected = false
private lateinit var okButton: Button


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        testConnection()

        //--------Afficher Pseudo par défaut---------------
        // Obtient l'instance des préférences partagées
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Affiche le dernier pseudo de l'historique s'il n'est pas vide, sinon affiche le pseudo par défaut des préférences
        val pseudoSet = sharedPreferences.getStringSet("pseudo_history", HashSet())
        val lastPseudo = pseudoSet?.lastOrNull() ?: sharedPreferences.getString("default_pseudo", "")

        val pseudoTextView = findViewById<TextView>(R.id.pseudo_input)
        val passwordTextView = findViewById<TextView>(R.id.password_input)
        pseudoTextView.text = lastPseudo





        okButton = findViewById<Button>(R.id.button)
        // Définit l'OnClickListener pour le bouton OK
        okButton.setOnClickListener {
            // Récupère le pseudo entré par l'utilisateur
            val pseudo = pseudoTextView.text.toString()
            val password = passwordTextView.text.toString()


            // Vérifie si un pseudo a été renseigné et si la connexion à l'API est valide
            if (pseudo.isNotEmpty() ) {

                DataProvider.getHash(
                    this,
                    user = pseudo,
                    password = password,
                    onSuccess = { hash ->
                        // Connexion réussie
                        this@MainActivity.runOnUiThread {
                            handleConnection(hash)


                            //Ouverture d'une nouvelle activité
                            val intent = Intent(this, ChoixListActivity::class.java)
                            intent.putExtra("pseudo", pseudo)
                            startActivity(intent)
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


                // Sauvegarde l'historique des pseudos dans les préférences partagées
                pseudoSet?.add(pseudo)
                val editor = sharedPreferences.edit()
                editor.putStringSet("pseudo_history", pseudoSet)
                editor.apply()


            }
        }
    }

    private fun handleConnection(hash : String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putString("token", hash as String)
        editor.apply()
        Log.e("MainActivity","API Connected with hash :$hash")
    }

    private fun handleApiError(error: Throwable) {
        // Afficher un message d'erreur ou effectuer d'autres actions appropriées
        Log.e("MainActivity", "Erreur de connexion à l'API: ${error.message}")

        //Afficher à l'écran l'erreur
    }

    private fun testConnection() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            webConnected = true
        } else {
            okButton.isEnabled = false
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




