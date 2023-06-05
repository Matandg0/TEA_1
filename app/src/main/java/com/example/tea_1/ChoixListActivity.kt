package com.example.tea_1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChoixListActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)



        val pseudo = intent.getStringExtra("pseudo")

        // ------ Affichage de la liste associée au pseudo ----
        // Charger la liste à partir des SharedPreferences
        val loadedList = ListProfile.loadList(this, pseudo)

        // Extraire les noms de chaque élément de loadedList
        val namesList = loadedList?.map { it.Nom }
        val itemList = namesList?.mapIndexed { index, name ->
            ListProfile.Item(index + 1, name)
        }

        // Affichage de la liste chargée
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = PostAdapter(dataSet = itemList, listener = object : OnItemClickListener {
            override fun onItemClick(itemId: Int) {
                val intent = Intent(this@ChoixListActivity, ShowListActivity::class.java)
                intent.putExtra("itemId", itemId)
                intent.putExtra("pseudo", pseudo)
                startActivity(intent)
            }
        })
        list.layoutManager = LinearLayoutManager(this)



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
        private val dataSet: List<ListProfile.Item>?,
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
            holder.bind(elt?.name)

            holder.itemView.setOnClickListener {
                if (elt != null) {
                    listener.onItemClick(elt.id)
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