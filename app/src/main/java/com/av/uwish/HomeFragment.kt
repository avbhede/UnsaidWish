package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aig.extracoaching.Adapter.HomeVideoAdapter
import com.av.uwish.Model.HomeVideoModel
import com.av.uwish.Retrofit.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import java.io.IOException
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */

class HomeFragment : Fragment() {

    lateinit var v1:VideoView

    lateinit var listview: ListView
    lateinit var userid:String
    lateinit var homeModel:java.util.ArrayList<HomeVideoModel>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progress_spinner: Dialog
    lateinit var homeAdapter:HomeVideoAdapter
    lateinit var mobile:String
    lateinit var pass:String
    lateinit var message_inbox:ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview = inflater.inflate(R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment

        listview = rootview.findViewById(R.id.home_vdo_list)
        message_inbox = rootview.findViewById(R.id.back_profile)

        homeModel= ArrayList()

        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        Log.v("HOMEDATA",sharedPreferences.getString("mobile","").toString()+" DFDF")
        userid = sharedPreferences.getString("id","").toString()
        mobile = sharedPreferences.getString("mobile","").toString()
        pass   = sharedPreferences.getString("password","").toString()



        getHomeVideo()
        serverAuthentication()

        message_inbox.setOnClickListener {

            val activity = context as AppCompatActivity
            val myFragment: Fragment = Search()
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.maincontent, myFragment)
                .replace(R.id.maincontent, myFragment).addToBackStack(null)
                .commit()

        }

        return rootview

    }


    private fun serverAuthentication() {

        Log.v("USER_DATA",mobile+" "+pass+" ")

        val call = RetrofitClient.instance.api.login(mobile,pass)
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
                        var password:String=jsonObject.getString("password")
                        var doj:String=jsonObject.getString("joining_date")
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
                        editor.putString("password", pass)
                        editor.putString("state", state)
                        editor.putString("city", city)
                        editor.putString("password", password)
                        editor.putString("country", country)
                        editor.putString("photo", photo)
                        editor.putBoolean("isLoggedIn", true)
                        editor.commit()


                    } else {

                        // progress_spinner.dismiss()
                        Toast.makeText(activity, "Invalid User Name or Password", Toast.LENGTH_LONG).show()

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


    fun getHomeVideo()
    {
        progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
        val view = LayoutInflater.from(activity).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.getHomeVideo(userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    if (s != null) {

                        val jsonObject = JSONObject(s)

                        if (jsonObject.getBoolean("status")) {
                            val jsonArray = jsonObject.getJSONArray("data")

                            if (jsonArray.length() > 0) {

                                for (i in 0 until jsonArray.length())
                                {
                                    val c = jsonArray.getJSONObject(i)

                                    val modelObject = HomeVideoModel()


                                    modelObject.v_id = c.getString("vid")
                                    modelObject.vdo_url = c.getString("video")
                                    //modelObject.uid = c.getString("user_id")
                                    modelObject.uid = c.getString("user_id")
                                    // modelObject.vdo_url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                                    modelObject.post_date= c.getString("postdate")
                                    modelObject.post_name = c.getString("name")
                                    modelObject.like_count = c.getString("likes")
                                    modelObject.deslike_count = c.getString("dislikes")
                                    modelObject.report = c.getString("report")
                                    modelObject.like_status = c.getString("user_response")
                                    modelObject.follow_status = c.getString("follow_status")
                                    modelObject.post_profile = c.getString("profile_pic")

                                    Log.v("PRofile",modelObject.post_profile)
                                    Log.v("Follow Status",modelObject.follow_status)

                                    Log.v("VIDEO URL",c.getString("video"))

                                    homeModel.add(modelObject)

                                }

                                set_bhk_adapter_list()

                            } else {

                                progress_spinner.dismiss()
                                Toast.makeText(activity, "Post not available !", Toast.LENGTH_SHORT).show()
                            }

                            progress_spinner.dismiss()

                        } else {

                            progress_spinner.dismiss()
                            Toast.makeText(activity, " Post not available !", Toast.LENGTH_SHORT).show()
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
    }


    fun set_bhk_adapter_list() {
        try {
            homeAdapter = HomeVideoAdapter(context!!, R.layout.home_video_layout, homeModel)
            listview.adapter = homeAdapter
            homeAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }

}
