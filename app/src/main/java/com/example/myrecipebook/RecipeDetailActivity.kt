package com.example.myrecipebook

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrecipebook.data.Recipe
import com.example.myrecipebook.model.NewRecipeAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recipe_detail.*

class RecipeDetailActivity : AppCompatActivity() {

    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)


        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Recipe Detail"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        var extras = intent.extras
        var recipeName = extras?.get("recipeName")
        var recipeDescription = extras?.get("recipeDescription")
        var recipeIngredients = extras?.get("recipeIngredients")
        var recipePic = extras?.get("recipePic")
        var key = extras?.get("key")

        recipeTitleDetail.text = recipeName.toString()
        recipeDescriptionDetail.text = recipeDescription.toString()
        recipeIngredientsDEtail.text = recipeIngredients.toString()
        Glide.with(this).load(recipePic).into(image_detail)


        delete.setOnClickListener {
            databaseReference = FirebaseDatabase.getInstance().getReference("Recipe").child(key.toString())
            databaseReference?.removeValue()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        edit.setOnClickListener(
            View.OnClickListener {
                var intent = Intent(this, EditActivity::class.java)
                intent.putExtra("recipeName",recipeName.toString() )
                intent.putExtra("recipeDescription",recipeDescription.toString() )
                intent.putExtra("recipeIngredients",recipeIngredients.toString())
                intent.putExtra("recipePic",recipePic.toString() )
                intent.putExtra("key",key.toString())
                startActivity(intent)

            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

