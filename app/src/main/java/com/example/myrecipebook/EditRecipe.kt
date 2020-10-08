package com.example.myrecipebook

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myrecipebook.data.Recipe
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import java.io.IOException
import java.text.DateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null;
    var imageUrl: String? = null
    var filePath: Uri? = null
    var storageReference: StorageReference? = null
    var databaseReference: DatabaseReference? = null
    var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        progressDialog = ProgressDialog(this)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Update Recipe"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)


        var extras = intent.extras
        var recipeNameOld = extras?.get("recipeName")
        var recipeDescriptionOld = extras?.get("recipeDescription")
        var recipeIngredientsOld = extras?.get("recipeIngredients")
        var recipePicOld = extras?.get("recipePic")
        var keyOld = extras?.get("key")

        updatRecipeName.setText(recipeNameOld.toString())
        updateDescription.setText(recipeDescriptionOld.toString())
        updateIngredients.setText(recipeIngredientsOld.toString())
        imageUrl = recipePicOld.toString()
        key = keyOld.toString()
         Glide.with(this).load(recipePicOld).into(updateImage)


        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe").child(key.toString())

        updateImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        updateBtn.setOnClickListener {
            updateReceipe()
        }
    }

    fun updateImage(){
        progressDialog?.show();
        storageReference = FirebaseStorage.getInstance().reference.child("RecipeImage")

        val uploadTask = storageReference?.putFile(filePath!!)
        uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageReference!!.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                imageUrl  = downloadUri.toString()
                updateReceipe()
                progressDialog?.dismiss()
            } else {
                // Handle failures
            }
        }?.addOnFailureListener{
            progressDialog?.dismiss()
        }

    }

    fun updateReceipe(){


        progressDialog?.show();
        var recipe = Recipe(
            updatRecipeName.text.toString(),
            updateDescription.text.toString(),
            updateIngredients.text.toString() ,
            imageUrl.toString(),
            key.toString()
        )


        databaseReference!!.setValue(recipe).addOnCompleteListener { task ->
            progressDialog?.dismiss()
            if (task.isSuccessful) {
                Log.d("test", "Success")
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("test", "Failed")
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                updateImage?.setImageBitmap(bitmap)
                updateImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
