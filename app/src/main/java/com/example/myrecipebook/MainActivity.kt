package com.example.myrecipebook

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.data.Recipe
import com.example.myrecipebook.model.NewRecipeAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var adapterRecipe: NewRecipeAdapter? = null
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    var progressDialog: ProgressDialog? = null
    var recipeList = ArrayList<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)

        progressDialog!!.show()
        progressDialog!!.setMessage("Web Searching...")
        adapterRecipe = NewRecipeAdapter(this, recipeList)
        recyclerView_id.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_id.adapter = adapterRecipe

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe")



        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                recipeList.clear()
                dataSnapshot.children.mapNotNullTo(recipeList) { it.getValue<Recipe>(Recipe::class.java) }
                adapterRecipe?.notifyDataSetChanged()
                progressDialog!!.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        eventListener = databaseReference?.addValueEventListener(menuListener)

        addEvent.setOnClickListener {
            var intent = Intent(this, NewRecipe::class.java)
            startActivity(intent)
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
        })

    }

    fun filter(value: String){
        val filterList = ArrayList<Recipe>()


        for (item: Recipe in recipeList) {
            if(item.title!!.toLowerCase(Locale.ROOT).contains(value) || item.title!!.contains(value)){
                filterList.add(item)
            }
        }

        adapterRecipe?.filteredList(filterList)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}

