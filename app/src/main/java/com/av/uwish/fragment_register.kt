package com.av.uwish


import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.av.uwish.Retrofit.RetrofitClient
import com.hbb20.CountryCodePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import java.io.IOException
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class fragment_register : Fragment() {

    lateinit var fname: EditText
    lateinit var mname: EditText
    lateinit var lname: EditText
    lateinit var dob: EditText
    lateinit var email: EditText
    lateinit var mobile: EditText
    lateinit var male:RadioButton
    lateinit var female:RadioButton
    lateinit var other:RadioButton
    lateinit var country: EditText
    lateinit var state: EditText
    lateinit var city: EditText
    lateinit var passsword: EditText
    lateinit var cf_pass: EditText
    lateinit var signup: Button
    lateinit var gotologin:TextView

    lateinit var ccp: CountryCodePicker
    lateinit var sharedPreferences: SharedPreferences

    lateinit var fname_str:String
    lateinit var mname_str:String
    lateinit var lname_str:String
    lateinit var dob_str:String
    lateinit var email_str:String
    lateinit var country_str:String
    lateinit var state_str:String
    lateinit var city_str:String
    lateinit var pass_str:String
    lateinit var cf_pass_str:String
    lateinit var userid:String
    lateinit var mobile_str:String
    lateinit var gender:String

    lateinit var progress_spinner: Dialog

    lateinit var locationManager: LocationManager
    lateinit var latitude:String
    lateinit var longitude:String
    private val REQUEST_LOCATION = 1


    var mYear:Int?=null
    var mMonth:Int?=null
    var mDay:Int?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview = inflater.inflate(R.layout.fragment_register, container, false)


        fname = rootview.findViewById(R.id.et_fname)
        mname = rootview.findViewById(R.id.et_mname)
        lname = rootview.findViewById(R.id.et_lname)
        email = rootview.findViewById(R.id.et_email)
        mobile = rootview.findViewById(R.id.et_mobile)
        male = rootview.findViewById(R.id.male)
        female = rootview.findViewById(R.id.female)
        other = rootview.findViewById(R.id.other)
        passsword = rootview.findViewById(R.id.et_password)
        cf_pass = rootview.findViewById(R.id.et_repassword)
        dob = rootview.findViewById(R.id.et_dob)
        state = rootview.findViewById(R.id.et_state)
        city = rootview.findViewById(R.id.et_city)
        country = rootview.findViewById(R.id.et_country)
        signup = rootview.findViewById(R.id.btn_register)
        ccp = rootview.findViewById(R.id.countrycode)


        gotologin = rootview.findViewById(R.id.gotologin)

        locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            OnGPS()
        }

        getLocation()

        dob.setOnClickListener {


            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            var month:Int = mMonth!!

            val datePickerDialog = DatePickerDialog(context!!,
                object: DatePickerDialog.OnDateSetListener {

                    override fun onDateSet(view: DatePicker, year:Int, monthOfYear:Int, dayOfMonth:Int) {

                        var month= monthOfYear + 1
                        dob.setText("$dayOfMonth-$month-$year")

                    }

                }, mYear!!, month, mDay!!)

           // datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

        }


       gotologin.setOnClickListener {

           val fm = activity!!.supportFragmentManager
           val fragment = fragment_login()
           fm.beginTransaction().add(R.id.mainlayout, fragment).commit()
           fm.beginTransaction().replace(R.id.mainlayout, fragment).commit()
           fm.beginTransaction().addToBackStack(null)

       }

        signup.setOnClickListener {

            fname_str= fname.text.toString()
            mname_str= mname.text.toString()
            lname_str= lname.text.toString()
            email_str= email.text.toString()
            dob_str= dob.text.toString()



            if(ccp.selectedCountryCode!="91")
            {
                mobile_str= "91"+mobile.text.toString()
            }else
            {
                mobile_str = ccp.selectedCountryCode+""+mobile.text.toString()
            }


            if(male.isChecked)
            {
                gender = "M"

            }else if(female.isChecked)
            {
                gender = "F"

            }else if(other.isChecked)
            {
                gender = "O"
            }

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
            else if (dob_str.equals("")) {

                dob.setError("Select DOB")

            }
            else if (city_str.equals("")) {

                city.setError("Enter City")

            }
            else if (country_str.equals("")) {

                email.setError("Enter Country")

            }
            else if (mobile_str.equals("")) {

                mobile.setError("Enter Mobile")

            }
            else if(!isNetworkConnected())
            {

                val dialog = android.app.AlertDialog.Builder(activity)

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
                Signup()

            }


        }

        return rootview
    }


    private fun getLocation()
    {
        //Check Permissions again
        if ((ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)))
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
        else
        {
            val LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            if (LocationGps != null)
            {
                val lat = LocationGps.getLatitude()
                val longi = LocationGps.getLongitude()
                latitude = (lat).toString()
                longitude = (longi).toString()

                Log.v("Location LAT ",latitude +" " +longitude )

                try
                {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        LocationGps.getLatitude(),
                        LocationGps.getLongitude(), 1)


                    Log.v("Location GPS with text",addresses.get(0).subAdminArea)
                    Log.v("Location GPS with text",addresses.get(0).toString())
                    Log.v("Location GPS with text",addresses.get(0).countryName)
                    Log.v("Location GPS with text",addresses.get(0).adminArea)
                    Log.v("Location GPS with text",addresses.get(0).featureName)

                    state_str= addresses.get(0).adminArea
                    city_str= addresses.get(0).subAdminArea
                    country_str= addresses.get(0).countryName

                }
                catch (e:Exception) {
                }

            }
            else if (LocationNetwork != null)
            {
                val lat = LocationNetwork.getLatitude()
                val longi = LocationNetwork.getLongitude()
                latitude = (lat).toString()
                longitude = (longi).toString()
                //Toast.makeText(this@HomeActivity, " LAT "+ latitude , Toast.LENGTH_SHORT).show()
                Log.v("Location LAT ",latitude +" " +longitude )

                try
                {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        LocationNetwork.getLatitude(),
                        LocationNetwork.getLongitude(), 1)



                    Log.v("Location NET with text",addresses.get(0).subAdminArea)
                    Log.v("Location NET with text",addresses.get(0).toString())
                    Log.v("Location NET with text",addresses.get(0).countryName)
                    Log.v("Location NET with text",addresses.get(0).adminArea)
                    Log.v("Location NET with text",addresses.get(0).featureName)

                    state_str= addresses.get(0).adminArea
                    city_str= addresses.get(0).subAdminArea
                    country_str= addresses.get(0).countryName

                }
                catch (e:Exception) {
                }
            }
            else if (LocationPassive != null)
            {
                val lat = LocationPassive.getLatitude()
                val longi = LocationPassive.getLongitude()
                latitude = (lat).toString()
                longitude = (longi).toString()
                Log.v("Location LAT ",latitude +" " +longitude )

            }
            else
            {
            }

                    }
    }


    private fun OnGPS() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which:Int) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }).setNegativeButton("NO", object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which:Int) {
                dialog.cancel()
            }
        })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun Signup()
    {

        Log.v("SIGNUP",fname_str+" "+mname_str+" "+lname_str+" "+mobile_str+" "+email_str+" "+city_str+" "+state_str+" "+country_str+" "+dob_str+" "+gender)

        progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
        val view = LayoutInflater.from(activity).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.add_user(fname_str,mname_str,lname_str,mobile_str,email_str,city_str,state_str,country_str,dob_str,gender)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        progress_spinner.dismiss()
                        //Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                        getOtp()

                    }
                    else {

                        progress_spinner.dismiss()
                        Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

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


    fun getOtp()
    {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.otp_verify_layout)
        dialog.setCancelable(true)


        var tv_otp=(dialog.findViewById(R.id.et_otp) as EditText)


        (dialog.findViewById(R.id.btn_verify) as Button).setOnClickListener {


            try {
                //show dialog

                val call = RetrofitClient.instance.api.login(mobile_str,tv_otp.text.toString())
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        try {
                            val s = response.body()!!.string()
                            if (s != null) {

                                val jsonObject = JSONObject(s)

                                Log.v("STATUS",jsonObject.getBoolean("status").toString())

                                if (jsonObject.getBoolean("status")) {

                                    dialog.dismiss()
                                    Toast.makeText(context, "OTP Verified", Toast.LENGTH_LONG).show()

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


                                    var intent= Intent(activity,MainActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    dialog.dismiss()
                                    Toast.makeText(context, "Invalid OTP", Toast.LENGTH_LONG).show()
                                }

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


            } catch (ex: WindowManager.BadTokenException) {
                ex.printStackTrace()
            }


        }


        dialog.show()


    }


    // Check Internet Connection

    fun isNetworkConnected():Boolean {
        val cm =context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()
    }



}
