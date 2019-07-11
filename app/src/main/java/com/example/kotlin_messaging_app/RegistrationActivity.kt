package com.example.kotlin_messaging_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kotlin_messaging_app.VO.UserVO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity()
{

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        btnRegister_SignIn.setOnClickListener {

            Log.d("RegistrationActivity: ", "Go back to LoginActivity.")
            finish()
        }

        btnRegister.setOnClickListener {

            performRegister()
        }

        btn_insert_img.setOnClickListener {
            Log.d("RegistrationActivity: ", "Select profile image.")

            val intent = Intent (Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //Set android status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.statusBarColor = ContextCompat.getColor(this, R.color.layout_bg_color)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        }

    }

    var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {
            //Proceed and check what is the selected image.
            Log.d("RegistrationActivity: ", "Image was selected.")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circleImageView.setImageBitmap(bitmap)
            btn_insert_img.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            btn_insert_img.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister()
    {
        val username = txtRegister_Username.text.toString()
        val email = txtRegister_Email.text.toString()
        val password = txtRegister_Password.text.toString()
//        val confirmPassword = txtRegister_Confirm_Password.text.toString()

        if (username.isEmpty())
        {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        else if (email.isEmpty())
        {
            Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        else if (password.isEmpty())
        {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegistrationActivity", "Username: $username")
        Log.d("RegistrationActivity", "Email: $email")
        Log.d("RegistrationActivity", "Password: $password")

        //Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("RegistrationActivity", "Successfully created a user with id: ${it.result.user.uid}")

                uploadImageToFirebaseStorage()

                Toast.makeText(this, "Register Successful, Welcome!", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
            .addOnFailureListener {
                Log.d("RegistrationActivity", "Failed to register new user: ${it.message}")
                Toast.makeText(this, "Failed to register new user: ${it.message}", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun uploadImageToFirebaseStorage()
    {
        if (selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegistrationActivity: ", "Successfully uploaded image to Firebase: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.d("RegistrationActivity", "File Located at: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                //do some logging here
                Log.d("RegistrationActivity", "Image couldn't be upload to cloud.")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String)
    {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = UserVO(uid, txtRegister_Username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("RegistrationActivity", "User data saved to Firebase Database!")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegistrationActivity", "User data couldn't save to Firebase Database!: ${it.message}")
            }
    }
}

/*class User (val uid: String, val username: String, val profileimageUrl: String)
{
    constructor(): this("","","")
}*/
