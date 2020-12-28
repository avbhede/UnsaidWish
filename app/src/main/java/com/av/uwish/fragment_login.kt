package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.av.uwish.Retrofit.RetrofitClient
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hbb20.CountryCodePicker
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class fragment_login : Fragment() {


    lateinit var username_et: EditText
    lateinit var password_et:EditText
    lateinit var loginBtn:Button
    lateinit var otpBtn:Button
    lateinit var gotoReg:TextView


    lateinit var sharedPreferences: SharedPreferences
    lateinit var progress_spinner: Dialog
    lateinit var goto_home: TextView

    lateinit var uname:String
    lateinit var pass:String
    lateinit var Token:String
    lateinit var signInButton:SignInButton
    lateinit var facebookLogin: LoginButton
    lateinit var mGoogleSignInClient:GoogleSignInClient
    lateinit var callbackManager:CallbackManager
    lateinit var accessToken:AccessToken

    lateinit var ccp:CountryCodePicker

    companion object {
        private const val RC_SIGN_IN  = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview= inflater.inflate(R.layout.fragment_login, container, false)

        loginBtn = rootview.findViewById(R.id.btn_login)
        otpBtn = rootview.findViewById(R.id.btn_otp)
        username_et = rootview.findViewById(R.id.et_email)
        password_et = rootview.findViewById(R.id.et_password)
        signInButton = rootview.findViewById(R.id.sign_in_button)
        facebookLogin = rootview.findViewById(R.id.login_button)
        ccp = rootview.findViewById(R.id.countrycode)
        gotoReg = rootview.findViewById(R.id.reg)


        callbackManager = CallbackManager.Factory.create()

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()


        gotoReg.setOnClickListener {

            val fm = activity!!.supportFragmentManager
            val fragment = fragment_register()
            fm.beginTransaction().add(R.id.mainlayout, fragment).commit()
            fm.beginTransaction().replace(R.id.mainlayout, fragment).commit()
            fm.beginTransaction().addToBackStack(null)

        }
        // Facebook Signin

        // Callback registration
        facebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                accessToken = loginResult!!.accessToken
                var url:String = "https://graph.facebook/"+loginResult!!.accessToken.userId+"picture?return_ssl_resources=1"
                editor.putString("id", loginResult!!.accessToken.userId)
                editor.putString("photo",url)
                editor.commit()

                Log.v("USERID TOKEN",accessToken.toString()+" kkk")


                val request: GraphRequest = GraphRequest.newMeRequest(accessToken,
                    object : GraphRequest.GraphJSONObjectCallback {

                        override fun onCompleted(`object`: JSONObject?,response: GraphResponse?) {

                            // Application code
                            try {

                                Log.i("Response", response.toString())
                                val email = response!!.jsonObject.getString("email")
                                val firstName =response.jsonObject.getString("first_name")
                                val lastName =response.jsonObject.getString("last_name")
                                val gender = response.jsonObject.getString("gender")
                                val dateob = response.jsonObject.getString("birthday")
                                val profile = Profile.getCurrentProfile()
                                val id = profile.id
                                val link = profile.linkUri.toString()

                                sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()

                                editor.putString("fname", firstName)
                                editor.putString("lname", lastName)
                                editor.putString("email", email)
                                editor.putString("dob", dateob)

                                editor.commit()

                                Log.i("Link", link)

                                if (Profile.getCurrentProfile() != null) {

                                    Log.i("Login","ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200))
                                }
                                Log.i("Login" + "Email", email)
                                Log.i("Login" + "FirstName", firstName)
                                Log.i("Login" + "LastName", lastName)
                                Log.i("Login" + "Gender", gender)

                                var intent= Intent(activity,MainActivity::class.java)
                                startActivity(intent)

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }


                        }
                    })

                val parameters = Bundle()
                parameters.putString("fields", "id,name,link")
                request.setParameters(parameters)
                request.executeAsync()


                // App code
            }

            override fun onCancel() {
                // App code
                Log.v("LOGIN_CANCEL","ERROR")
            }

            override fun onError(exception: FacebookException?) {
                // App code
                Log.v("LOGIN_CANCEL","ERROR")
            }
        })




        // Google Signin

        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        signInButton.setOnClickListener {

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

        otpBtn.setOnClickListener {

            if(ccp.selectedCountryCode!="91")
            {
                uname = "91"+username_et.text.toString()

            }else
            {
                uname = ccp.selectedCountryCode+""+username_et.text.toString()
            }


            Log.v("CCP",ccp.selectedCountryCode)
            Log.v("CCP_NUM",uname)


            if (uname.equals("")) {
                username_et.setError("Enter username")
            }
            else if(!isNetworkConnected())
            {

                val dialog = AlertDialog.Builder(context!!)

                dialog.setTitle("Enable Internet Connection")
                    .setMessage("Your Internet  is set to 'Off'.\nPlease Enable Mobile Data")
                    .setPositiveButton("Data Settings") { _, _ ->
                        val myIntent = Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
                        startActivity(myIntent)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                   }

                dialog.show()
            }
            else
            {
                getOtp(uname)
            }

        }


        loginBtn.setOnClickListener {

            if(ccp.selectedCountryCode!="91")
            {
                uname = "91"+username_et.text.toString()

            }else
            {
                uname = ccp.selectedCountryCode+""+username_et.text.toString()
            }

            Log.v("CCP",ccp.selectedCountryCode)
            Log.v("CCP_NUM",uname)

            pass = password_et.text.toString()

            Log.v("UN OTP",uname+" "+pass+" checkOTP")

            if (uname.equals("")) {
                 username_et.setError("Enter username")
            }
            else if (pass.equals("")) {
                password_et.setError("Enter Password")
            }
            else if(!isNetworkConnected())
            {

                val dialog = AlertDialog.Builder(context!!)

                dialog.setTitle("Enable Internet Connection")
                    .setMessage("Your Internet  is set to 'Off'.\nPlease Enable Mobile Data")
                    .setPositiveButton("Data Settings") { _, _ ->
                        val myIntent = Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
                        startActivity(myIntent)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }

                dialog.show()
            }
            else
            {

                serverAuthentication()
            }

        }

        return rootview

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult<ApiException>(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            startActivity(Intent(activity, MainActivity::class.java))

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error","signInResult:failed code=" + e.getStatusCode())
            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
        }

    }



    private fun getOtp(mobile:String) {

        try {

            progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
            val view = LayoutInflater.from(activity).inflate(R.layout.aux_progress_spinner, null)
            progress_spinner.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
            progress_spinner.window!!.setBackgroundDrawableResource(R.color.transparent)
            progress_spinner.setContentView(view)
            progress_spinner.show()

        } catch (e: NullPointerException) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val call = RetrofitClient.instance.api.getOtp(mobile)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    val jsonObject = JSONObject(s)

                    // Log.v("STATUS "," "+jsonObject.getBoolean("status"))

                    if (jsonObject.getBoolean("status")) {

                        otpBtn.visibility=View.GONE
                        loginBtn.visibility=View.VISIBLE
                        password_et.visibility=View.VISIBLE

                        progress_spinner.dismiss()
                        Toast.makeText(activity,jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                    } else {

                        progress_spinner.dismiss()
                        Toast.makeText(activity, "Invalid User Name", Toast.LENGTH_LONG).show()
                    }


                } catch (e: NullPointerException) {
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

        })
    }

    private fun serverAuthentication() {

        try {

            progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
            val view = LayoutInflater.from(activity).inflate(R.layout.aux_progress_spinner, null)
            progress_spinner.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
            progress_spinner.window!!.setBackgroundDrawableResource(R.color.transparent)
            progress_spinner.setContentView(view)
            progress_spinner.show()

        } catch (e: NullPointerException) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.v("Serverauthentication",uname+" "+pass+" API")

        val call = RetrofitClient.instance.api.login(uname,pass)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    val jsonObject = JSONObject(s)

                    // Log.v("STATUS "," "+jsonObject.getBoolean("status"))

                    if (jsonObject.getBoolean("status")) {

                        var id:String=jsonObject.getString("id")
                        var fname=jsonObject.getString("fname")
                        var mname=jsonObject.getString("mname")
                        var lname=jsonObject.getString("lname")
                        var mobile:String=jsonObject.getString("mobile")
                        var email=jsonObject.getString("email")
                        var state:String=jsonObject.getString("state")
                        var city:String=jsonObject.getString("city")
                        var country:String=jsonObject.getString("country")
                        var dob:String=jsonObject.getString("dob")
                        var doj:String=jsonObject.getString("joining_date")
                        var password:String=jsonObject.getString("password")
                        var photo:String=jsonObject.getString("photo")

                        Log.v("PHOTO_LOGIN",photo+" "+jsonObject.getString("photo"))

                        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putString("id", id)
                        editor.putString("fname", fname)
                        editor.putString("mname", mname)
                        editor.putString("lname", lname)
                        editor.putString("mobile", mobile)
                        editor.putString("dob", dob)
                        editor.putString("doj", doj)
                        editor.putString("email", email)
                        editor.putString("state", state)
                        editor.putString("city", city)
                        editor.putString("country", country)
                        editor.putString("photo", photo)
                        editor.putString("password", password)
                        editor.putBoolean("isLoggedIn", true)
                        editor.putBoolean("isLoggedMobile", true)
                        editor.commit()


                        Toast.makeText(activity, " Welcome "+  fname + " " + lname , Toast.LENGTH_LONG).show()

                        progress_spinner.dismiss()

                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)


                    } else {

                        progress_spinner.dismiss()
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                        sharedPreferences = context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
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

                        editor.commit()


                    }

                } catch (e: NullPointerException) {
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

        })
    }



    fun isNetworkConnected():Boolean {
        val cm =context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()
    }

}
