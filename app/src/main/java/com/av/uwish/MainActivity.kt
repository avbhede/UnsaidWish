package com.av.uwish

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.av.uwish.Retrofit.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity()
{

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar
    lateinit var nav_view:NavigationView
    lateinit var drawer:DrawerLayout
    lateinit var alertDialog: android.app.AlertDialog

     var fname_str:String=""
     var mname_str:String=""
     var lname_str:String=""
     var dob_str:String=""
     var email_str:String=""
     var country_str:String=""
     var state_str:String=""
     var city_str:String=""
     var pass_str:String=""
     var cf_pass_str:String=""
     var userid:String=""
     var mobile_str:String=""
     var gender:String=""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         nav_view = findViewById(R.id.nav_view) as NavigationView
         drawer = findViewById(R.id.drawer_layout) as DrawerLayout
         toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar.setNavigationIcon(null)
       // setSupportActionBar(toolbar)

        initNavigationMenu()

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.setDrawerIndicatorEnabled(false)
        toggle.isDrawerIndicatorEnabled = false
        toggle.syncState()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(this@MainActivity)

        if (acct != null) {

            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            val token = acct.idToken

            fname_str = acct.displayName.toString()
            pass_str = acct.id.toString()
            email_str = acct.email.toString()

            Log.v("GOOGLE LOGIN",personName+" "+personGivenName+" "+personFamilyName+" "+personEmail+" "+personId+" "+personPhoto+" "+token)
            sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("id", personId)
            editor.putString("fname", personName)
            editor.putString("email", personEmail)
            editor.putString("password", personId)
            editor.putString("mobile", personId)
            editor.putBoolean("isLoggedMobile", false)
            editor.commit()

            Signup()

        }

        val myFab: FloatingActionButton = findViewById(R.id.fabcamera) as FloatingActionButton
        myFab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val fm = supportFragmentManager
                val fragment = Camera()
                fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                fm.beginTransaction().addToBackStack(null)

            }
        })


        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigation)

        val fm = supportFragmentManager
        val fragment = HomeFragment()
        fm.beginTransaction().add(R.id.maincontent, fragment).commit()
        fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
        fm.beginTransaction().addToBackStack(null)

        val mOnNavigationItemSelectedListener =
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {

                        R.id.navigation_home -> {

                            val fm = supportFragmentManager
                            val fragment = HomeFragment()
                            fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().addToBackStack(null)

                            return@OnNavigationItemSelectedListener true
                            finish()

                        }

                        R.id.navigation_wish -> {

                            val fm = supportFragmentManager
                            val fragment = WishFragment()
                            fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().addToBackStack(null)


                            return@OnNavigationItemSelectedListener true
                            finish()


                        }

                        R.id.navigation_camera -> {

                            val fm = supportFragmentManager
                            val fragment = Camera()
                            fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().addToBackStack(null)


                            return@OnNavigationItemSelectedListener true
                            finish()
                        }

                        R.id.navigation_inbox -> {

                            val fm = supportFragmentManager
                            val fragment = UserInbox()
                            fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().addToBackStack(null)


                            return@OnNavigationItemSelectedListener true
                            finish()

                        }

                        R.id.navigation_profile -> {

                            val fm = supportFragmentManager
                            val fragment = ProfileFragment()
                            fm.beginTransaction().add(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().replace(R.id.maincontent, fragment).commit()
                            fm.beginTransaction().addToBackStack(null)


                            return@OnNavigationItemSelectedListener true
                            finish()

                        }

                    }
                    false
                }


        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }


    fun openDrawer()
    {
        drawer.openDrawer(Gravity.RIGHT)
    }


    fun initNavigationMenu() {

        val headerView=nav_view.getHeaderView(0)

        drawer.closeDrawer(Gravity.RIGHT,false)

        val toggle:ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close) {

        }

        drawer.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {

            override fun onNavigationItemSelected(item: MenuItem): Boolean {


                if (item.itemId == R.id.about) {

                     //Toast.makeText(this@MainActivity, "About Button", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawers()
                    // get prompts.xml view
                    val li = LayoutInflater.from(this@MainActivity)
                    val promptsView = li.inflate(R.layout.helpsupport, null)
                    val alertDialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView)
                    alertDialogBuilder.setCancelable(true)

                    val cancel = promptsView.findViewById<ImageButton>(R.id.bt_close)

                    cancel.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    // create alert dialog
                    alertDialog = alertDialogBuilder.create()

                    // show it
                    alertDialog.show()

                }

                if (item.itemId == R.id.feedback) {


                    var intent= Intent(this@MainActivity, feedback::class.java)
                    startActivity(intent)
                    finish()

                }


                if (item.itemId == R.id.termpolicy) {

                    Toast.makeText(this@MainActivity, "Term policy Button", Toast.LENGTH_SHORT).show()
                }


                if (item.itemId == R.id.logout) {

                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Logout?")
                    builder.setPositiveButton("OK") { dialog, id ->

                        signOut()

                        val sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putString("id", "")
                        editor.putString("fname", "")
                        editor.putString("mname", "")
                        editor.putString("lname", "")
                        editor.putString("mobile", "")
                        editor.putString("dob", "")
                        editor.putString("doj", "")
                        editor.putString("email", "")
                        editor.putString("password", "")
                        editor.putString("state", "")
                        editor.putString("city", "")
                        editor.putString("country", "")
                        editor.putString("photo", "")
                        editor.putBoolean("isLoggedIn", false)
                        editor.putBoolean("isLoggedMobile", false)

                        editor.commit()

                        var intent= Intent(this@MainActivity, login_signup::class.java)
                        startActivity(intent)


                    }

                    builder.show()

                }

                return true
            }
        })

    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    fun Signup()
    {

        Log.v("SIGNUP",fname_str+" "+mname_str+" "+lname_str+" "+mobile_str+" "+email_str+" "+city_str+" "+state_str+" "+country_str+" "+dob_str+" "+gender+" "+pass_str)

        val call = RetrofitClient.instance.api.add_user(fname_str,mname_str,lname_str,pass_str,email_str,city_str,state_str,country_str,dob_str,gender)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        //progress_spinner.dismiss()
                       // Toast.makeText(this@MainActivity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                    }
                    else {

                       // Toast.makeText(this@MainActivity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                    }

                } catch (e: NullPointerException) {
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }



}
