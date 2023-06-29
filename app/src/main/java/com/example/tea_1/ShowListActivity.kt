package com.example.tea_1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.sampledata.DatabaseProvider
import fr.ec.app.data.DataProvider
import fr.ec.app.data.api.response.PostResponse
import kotlinx.coroutines.launch


class ShowListActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)


        val itemId = intent.getIntExtra("itemId", -1)
        val pseudo = intent.getStringExtra("pseudo")
        val webConnected = intent.getBooleanExtra("webconnected",false)

        val databaseProvider = DatabaseProvider(application)

        // ------ Affichage de la liste associée au pseudo ----
        // Charger la liste à partir des SharedPreferences
        if (webConnected){
            DataProvider.getData(
                this,
                "lists/$itemId/items",
                false,
                onSuccess = { list ->
                    this@ShowListActivity.runOnUiThread {
                        val loadedList = list


                        //Enregistrement Listes dans Base de données
                        lifecycleScope.launch {
                            try {
                                databaseProvider.savePosts(list,itemId)
                            } catch (e: Exception) {
                                Log.e("Activity", "Error saving lists: ${e.message}")
                            }

                            affichageListe(loadedList)

                        }

                    }
                },
                onError =  { error ->
                    this@ShowListActivity.runOnUiThread {
                        handleApiError(error)

                        lifecycleScope.launch {
                            try {
                                val loadedList = databaseProvider.getPosts(itemId)
                                affichageListe(loadedList)

                            } catch (e: Exception) {
                                Log.e("Activity", "Error saving lists: ${e.message}")
                            }


                        }
                    }

                }
            )
        } else {
            lifecycleScope.launch {
                try {
                    val loadedList = databaseProvider.getPosts(itemId)
                    affichageListe(loadedList)

                } catch (e: Exception) {
                    Log.e("Activity", "Error saving lists: ${e.message}")
                }


            }
        }


        val list = findViewById<RecyclerView>(R.id.listItem)



        // Ajoute item quand on clique sur le bouton
        val addButton = findViewById<Button>(R.id.addItemButton)

        // Définit l'OnClickListener pour le bouton OK
        addButton.setOnClickListener {
            // Récupère la nouvelle liste entrée par l'utilisateur
            val newItemView = findViewById<TextView>(R.id.newItem)
            val newItem = newItemView.text.toString()

            // Ajoute la nouvelle liste
            ListProfile.addEltToList(this,pseudo + "_" + itemId.toString(), newItem)

            recreate()
        }


    }

    private fun affichageListe(loadedList : List<PostResponse>){
        // Affichage de la liste chargée
        val list = findViewById<RecyclerView>(R.id.listItem)
        list.adapter = PostAdapter(dataSet = loadedList)
        list.layoutManager = LinearLayoutManager(this)

        Log.e("ChoixListActivity", "Liste recuperé")
    }



    class PostAdapter(
        private val dataSet: List<PostResponse>?,
    ) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet?.size ?: 0
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val elt = dataSet?.get(position)
            if (elt != null) {
                holder.bind(elt.label)
            }

        }

        inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(name: String?) {
                val title = itemView.findViewById<TextView>(R.id.post_item)
                title.text = name ?: ""
            }
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

    private fun handleApiError(error: Throwable) {
        // Afficher un message d'erreur ou effectuer d'autres actions appropriées
        Log.e("MainActivity", "Erreur de connexion à l'API: ${error.message}")

        //Afficher à l'écran l'erreur
    }
}