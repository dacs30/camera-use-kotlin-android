package com.example.a30_camera

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import java.util.*
import Configs.createUri
import android.media.Image
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnLayout
import java.io.File
import Configs.getDefaultProfileDir
import android.graphics.BitmapFactory
import com.example.a30_camera.databinding.ActivityMainBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var picturePreview: ImageView
    lateinit var takePictureBtn: Button
    lateinit var takePictureNew: Button

    private lateinit var photoName: String
    private lateinit var logName: String

    val REQUEST_IMAGE_CAPTURE = 100

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        Log.d(TAG, "didTakePhoto: $didTakePhoto")
        if (didTakePhoto) {
            Log.d(TAG, "Successfully took a picture and saved to $photoName")
            val photoFile = photoName?.let {
                File(getDefaultProfileDir(this), it)
            }
            if (photoFile?.exists() == true){
                Log.d(TAG, "Location: $photoFile")
                Picasso.get()
                    .load(photoFile)
                    .into(picturePreview, object : Callback.EmptyCallback(){
                        override fun onSuccess() {
                            Log.d(TAG, "success")
                        }

                        override fun onError(e: Exception?) {
                            super.onError(e)
                            Log.e(TAG, e.toString())
                        }
                    })
            } else {
                picturePreview.setImageBitmap(null)
                picturePreview.tag = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        picturePreview = binding.picturePreview
        takePictureBtn = binding.takePictureBtn
        takePictureNew = binding.takePictureNew

        // old way of doing -> deprecated
        takePictureBtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Error: " + e.localizedMessage, Toast.LENGTH_SHORT)
            }
        }

        takePictureNew.setOnClickListener { view: View ->
            val timestamp = "${Date()}"
            photoName = "IMG_${timestamp}.JPG"
            logName = "${timestamp}.txt"

            // we want the photo to store inside the new profile/ subdirectory
            // the full path will be files/profile/IMG_XXX.JPG
            val photoUri = createUri(this, photoName)

            // note that, photoUri is used by the camera app to store the image
            // the syntax is similar to using explicit intent:
            // just passing in Intent instead of photoUri
            takePhoto.launch(photoUri)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val image = data?.extras?.get("data") as Bitmap
            picturePreview.setImageBitmap(image)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updatePhoto(photoName: String, view: ImageView ){
        // get the image from file system
        if (view.tag != photoName) {
            val photoFile = photoName?.let {
                File(getDefaultProfileDir(this), it)
            }

            if (photoFile?.exists() == true) {
//                view.doOnLayout { measuredView ->
//                    val scaledBitmap = getScaledBitmap(
//                        photoFile.path,
//                        measuredView.width,
//                        measuredView.height
//                    )
//                    view.setImageBitmap(scaledBitmap)
//                    view.tag = photoFileName
//                }
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                val bitmap = BitmapFactory.decodeFile(photoFile.path, options)
                view.setImageBitmap(bitmap)
                view.tag = photoName
            } else {
                view.setImageBitmap(null)
                view.tag = null
            }
        }
    }
}