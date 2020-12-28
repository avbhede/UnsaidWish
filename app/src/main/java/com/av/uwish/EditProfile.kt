package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.av.uwish.Retrofit.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import java.io.IOException
import org.json.JSONException
import org.json.JSONObject

class EditProfile : AppCompatActivity() {

    lateinit var fname:EditText
    lateinit var mname:EditText
    lateinit var lname:EditText
    lateinit var dob:EditText
    lateinit var email:EditText
    lateinit var country:EditText
    lateinit var state:EditText
    lateinit var city:EditText
    lateinit var passsword:EditText
    lateinit var new_pass:EditText
    lateinit var update:Button
    lateinit var back:ImageView

    lateinit var fname_str:String
    lateinit var mname_str:String
    lateinit var lname_str:String
    lateinit var dob_str:String
    lateinit var email_str:String
    lateinit var country_str:String
    lateinit var state_str:String
    lateinit var city_str:String
    lateinit var pass_str:String
    lateinit var new_pass_str:String
    lateinit var userid:String
    lateinit var mobile:String

    lateinit var sharedPreferences: SharedPreferences
    lateinit var progress_spinner:Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        fname= findViewById(R.id.et_fname)
        mname= findViewById(R.id.et_mname)
        lname= findViewById(R.id.et_lname)
        email= findViewById(R.id.et_email)
        state= findViewById(R.id.et_state)
        city= findViewById(R.id.et_city)
        country= findViewById(R.id.et_country)
        update = findViewById(R.id.btn_update)
        passsword = findViewById(R.id.et_password)
        new_pass = findViewById(R.id.et_repassword)
        back = findViewById(R.id.back_profile)


        sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

        fname.setText(sharedPreferences.getString("fname","").toString())
        mname.setText(sharedPreferences.getString("mname","").toString())
        lname.setText(sharedPreferences.getString("lname","").toString())
        email.setText(sharedPreferences.getString("email","").toString())
        state.setText(sharedPreferences.getString("state","").toString())
        country.setText(sharedPreferences.getString("country","").toString())
        city.setText(sharedPreferences.getString("city","").toString())
        passsword.setText(sharedPreferences.getString("password","").toString())


        mobile = sharedPreferences.getString("mobile","").toString()
        userid = sharedPreferences.getString("id","").toString()


        back.setOnClickListener {

            super.onBackPressed()

        }

        update.setOnClickListener {

            fname_str= fname.text.toString()
            mname_str= mname.text.toString()
            lname_str= lname.text.toString()
            email_str= email.text.toString()
            state_str= state.text.toString()
            city_str= city.text.toString()
            country_str= country.text.toString()
            pass_str = passsword.text.toString()
            new_pass_str = new_pass.text.toString()


            if (fname_str.equals("")) {

                fname.setError("Enter First Name")
            }
            else if (mname_str.equals("")) {

                mname.setError("Enter Middle Name")
            }
            else if (lname.equals("")) {

                lname.setError("Enter Last Name")
            }
            else if (state_str.equals("")) {

                state.setError("Enter State")
            }
            else if (email_str.equals("")) {

                email.setError("Enter Email")

            }
            else if (pass_str.equals("")) {

                passsword.setError("Enter Password")

            }
            else if (new_pass_str.equals("")) {

                new_pass.setError("Enter New Password")

            }
            else if (city_str.equals("")) {

                city.setError("Enter City")

            }
            else if (country_str.equals("")) {

                country.setError("Enter Country")

            }
            else if(!isNetworkConnected())
            {

                val dialog = android.app.AlertDialog.Builder(this@EditProfile)

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
                UpdateProfile()
            }


        }

    }

    override fun onBackPressed() {

        super.onBackPressed()
        this.finish()

    }


    fun UpdateProfile()
    {

        progress_spinner = Dialog(this@EditProfile, android.R.style.Theme_Black)
        val view = LayoutInflater.from(this@EditProfile).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.Update(userid,fname_str,mname_str,lname_str,email_str,city_str,state_str,country_str,new_pass_str)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        progress_spinner.dismiss()
                        Toast.makeText(this@EditProfile, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                        var intent= Intent(this@EditProfile,MainActivity::class.java)
                        startActivity(intent)

                    }
                    else {

                        progress_spinner.dismiss()
                        Toast.makeText(this@EditProfile, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

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


    // Check Internet Connection

    fun isNetworkConnected():Boolean {
        val cm =getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()
    }



}
