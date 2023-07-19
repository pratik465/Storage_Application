package com.example.storageapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storageapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var Uri: Uri
    var ImageCode = 12

    lateinit var storage: StorageReference
    lateinit var refDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance().reference
        refDB = FirebaseDatabase.getInstance().reference


        binding.btnSElect.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "Image/*"
            startActivityForResult(intent, ImageCode)
        }

        binding.btnUpload.setOnClickListener {
            val ref = storage.child("images/${Uri.lastPathSegment}.jpg")
            var uploadTask = ref.putFile(Uri)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    var key = refDB.root.push().key
                    refDB.root.child("Image").child(key!!).child("Image")
                        .setValue(downloadUri.toString())

                } else {

                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == ImageCode) {

                Uri = data?.data!!
                binding.imgImage.setImageURI(Uri)

            }
        }
    }

}