package com.av.uwish

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn


class Splash : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        val myThread = object : Thread() {

            override fun run() {

                try {

                    Thread.sleep(3000)

                     if(sharedPreferences.getBoolean("isLoggedIn",false) || account != null)
                     {

                         val intent = Intent(this@Splash, MainActivity::class.java)
                         startActivity(intent)
                         finish()

                     }
                     else{

                         val intent = Intent(this@Splash, login_signup::class.java)
                         startActivity(intent)
                         finish()

                     }

                    finish()

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

        }

        myThread.start()

    }

   /* override fun onStart() {

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {

            startActivity(Intent(this@Splash, MainActivity::class.java))
        }
        super.onStart()
    }

    */

}
