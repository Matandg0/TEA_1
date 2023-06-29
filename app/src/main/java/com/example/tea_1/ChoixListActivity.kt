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

class ChoixListActivity : AppCompatActivity() {

    private var pseudo : String? = null
    private var webConnected : Boolean = false
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val databaseProvider = DatabaseProvider(application)



        pseudo = intent.getStringExtra("pseudo")
        webConnected = intent.getBooleanExtra("webConnected",false)

        if (webConnected){
            DataProvider.getData(
                this,
                "lists",
                true,
                onSuccess = { list ->
                    this@ChoixListActivity.runOnUiThread {
                        val loadedList = list

                        //Enregistrement Listes dans Base de données
                        lifecycleScope.launch {
                            try {
                                databaseProvider.saveLists(list)

                            } catch (e: Exception) {
                                Log.e("Activity", "Error saving lists: ${e.message}")
                            }
                        }

                        affichageList(loadedList)

                    }
                },
                onError =  { error ->
                    this@ChoixListActivity.runOnUiThread {
                        handleApiError(error)

                        lifecycleScope.launch {
                            try {
                                val loadedList = databaseProvider.getLists()
                                affichageList(loadedList)


                            } catch (e: Exception) {
                                Log.e("Activity", "Error retrieving lists: ${e.message}")
                            }
                        }
                    }

                }
            )
        } else {

            lifecycleScope.launch {
                try {
                    val loadedList = databaseProvider.getLists()
                    affichageList(loadedList)


                } catch (e: Exception) {
                    Log.e("Activity", "Error retrieving lists: ${e.message}")
                }
            }
        }


        // ------ Récuperation des listes associés à ce pseudo ----- //

        // ------ Affichage de la liste associée au pseudo ----- //
        // Charger la liste à partir des SharedPreferences
        //val loadedList = ListProfile.loadList(this, pseudo)

        // Extraire les noms de chaque élément de loadedList
        //val namesList = loadedList.posts?.map { it.label }
        //val itemList = loadedList.posts?.mapIndexed { index, name ->
        //    ListProfile.Item(index + 1, name)
        //}





        // Ajoute liste quand on clique sur le bouton
        val addButton = findViewById<Button>(R.id.addListButton)

        // Définit l'OnClickListener pour le bouton OK
        addButton.setOnClickListener {
            // Récupère la nouvelle liste entrée par l'utilisateur
            val newListeView = findViewById<TextView>(R.id.newListe)
            val newListe = newListeView.text.toString()

            // Ajoute la nouvelle liste
            ListProfile.addEltToList(this,pseudo,newListe)

            recreate()
        }



    }

    private fun affichageList(loadedList : List<PostResponse>) {
        // Affichage de la liste chargée
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = PostAdapter(dataSet = loadedList, listener = object : OnItemClickListener {
            override fun onItemClick(itemId: Int) {
                val intent = Intent(this@ChoixListActivity, ShowListActivity::class.java)
                intent.putExtra("itemId", itemId)
                intent.putExtra("pseudo", pseudo)
                intent.putExtra("webConnected", webConnected)
                startActivity(intent)
            }
        })
        list.layoutManager = LinearLayoutManager(this)

        Log.e("ChoixListActivity", "Liste recuperé")
    }




    private fun handleApiError(error: Throwable) {
        // Afficher un message d'erreur ou effectuer d'autres actions appropriées
        Log.e("ChoixListActivity", "Erreur de récupération des listes: ${error.message}")

        //Afficher à l'écran l'erreur
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


    class PostAdapter(
        private val dataSet: List<PostResponse>?,
        private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list, parent, false)
            return PostViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet?.size ?: 0
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val elt = dataSet?.get(position)
            holder.bind(elt?.label)

            holder.itemView.setOnClickListener {
                if (elt != null) {
                    elt.id?.let { it1 -> listener.onItemClick(it1.toInt()) }
                }
            }
        }

        inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(name: String?) {
                val title = itemView.findViewById<TextView>(R.id.post_title)
                title.text = name ?: ""
            }
        }


    }

    interface OnItemClickListener {
        fun onItemClick(itemId: Int)
    }
}