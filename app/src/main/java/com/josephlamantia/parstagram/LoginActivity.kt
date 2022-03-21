package com.josephlamantia.parstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.ParseObject
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //check if user is logged in
        //if so, take them to MainActivity
        if (ParseUser.getCurrentUser() != null){
            goToMainActivity()
        }

        findViewById<Button>(R.id.login_button).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()
            loginUser(username, password)
        }

        findViewById<Button>(R.id.signupBtn).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()
            signUpUser(username, password)
        }

        //Below is for testing parse connection:
//        val firstObject = ParseObject("FirstClass")
//        firstObject.put("message","Hey ! First message from android. Parse is now connected")
//        firstObject.saveInBackground {
//            if (it != null){
//                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
//            }else{
//                Log.d("MainActivity","Object saved.")
//            }
//        }
    }

    private fun signUpUser(username: String, password: String){
        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // user has successfully created a new account
                Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Successfully signed up user!")
                goToMainActivity()
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                e.printStackTrace()
                Toast.makeText(this, "Error signing up!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(username: String, password: String){
        ParseUser.logInInBackground(username, password, ({ user, e ->
            if (user != null) {
                // Hooray!  The user is logged in.
                Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Successfully logged in user!")
                goToMainActivity()
            } else {
                // Signup failed.  Look at the ParseException to see what happened.
                e.printStackTrace()
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show()
            }})
        )
    }

    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val TAG = "LoginActivity"
    }
}