package com.example.myrecipebook.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrecipebook.R
import com.example.myrecipebook.RecipeDetailActivity
import com.example.myrecipebook.data.Recipe
import kotlinx.android.synthetic.main.adapter_recipe_list.view.*
import java.util.*

class NewRecipeAdapter(var mCtx: Context, private var myDataset: ArrayList<Recipe>) :
    RecyclerView.Adapter<NewRecipeAdapter.MyViewHolder>() {


    class MyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val recipeTitle = view.recipeTitle
        val recipeImage = view.recipe_img_id
        val card = view.card

    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recipe_list, parent, false)

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val recipe: Recipe = myDataset[position]
        holder.recipeTitle.text = recipe.title
        Glide.with(mCtx).load(recipe.thumbnail).into(holder.recipeImage)

//        holder.recipe_img_id.setImageResource(recipe.thumbnail)
        holder.card.setOnClickListener {

            var intent = Intent(mCtx, RecipeDetailActivity::class.java)
            intent.putExtra("recipeName",recipe.title )
            intent.putExtra("recipeDescription",recipe.description )
            intent.putExtra("recipeIngredients",recipe.ingredients )
            intent.putExtra("recipePic",recipe.thumbnail )
            intent.putExtra("key",recipe.key )
            mCtx.startActivity(intent)
        }
    }

    override fun getItemCount() = myDataset.size
    fun filteredList(filterList: ArrayList<Recipe>) {
        myDataset = filterList
        notifyDataSetChanged()
    }
}