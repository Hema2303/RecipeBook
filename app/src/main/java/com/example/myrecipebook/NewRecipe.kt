package com.example.myrecipebook

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myrecipebook.data.Recipe
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_new_recipe.*
import java.io.IOException
import java.text.DateFormat
import java.util.*

class NewRecipe : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null;
    var imageUrl: String? = null
    var filePath: Uri? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        progressDialog = ProgressDialog(this)



        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Add your Recipe here !"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)


        addRecipeImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        submitBtn.setOnClickListener {
            addRecipe()
        }



    }

    fun addRecipe(){
        progressDialog?.show();

        var currentDate: String? = null

        currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        var recipe = Recipe(
            recipeName.text.toString(),
            description.text.toString(),
            ingredients.text.toString(),
            imageUrl.toString(),
            currentDate
        )


        val database = FirebaseDatabase.getInstance()


        val myRef = database.getReference("Recipe").child(currentDate)



        myRef.setValue(recipe).addOnCompleteListener { task ->
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
                addRecipeImage?.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        progressDialog?.show()
        if(filePath != null){
            storageReference = FirebaseStorage.getInstance().reference
            val ref = storageReference?.child("RecipeImage")
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageUrl  = downloadUri.toString()
                    progressDialog?.dismiss()
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{
                progressDialog?.dismiss()
            }
        }else{
            progressDialog?.dismiss()
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

}
