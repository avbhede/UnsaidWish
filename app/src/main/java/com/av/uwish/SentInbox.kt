package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.av.uwish.Adapter.SentInboxAdapter
import com.av.uwish.Model.InboxModel
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SentInbox : AppCompatActivity() {


    lateinit var homeModel:java.util.ArrayList<InboxModel>
    lateinit var homeAdapter: SentInboxAdapter
    lateinit var listview: ListView
    lateinit var anim: LottieAnimationView
    lateinit var progress_spinner: Dialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userid:String
    lateinit var back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sent_inbox)

        listview = findViewById(R.id.sent_inbox_list)
        back = findViewById(R.id.back_inbox)
        anim = findViewById(R.id.animationView_sentinbox)


        homeModel= ArrayList()
        sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()


        getHomeVideo()

        back.setOnClickListener {

            super.onBackPressed()

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun getHomeVideo()
    {
        progress_spinner = Dialog(this@SentInbox, android.R.style.Theme_Black)
        val view = LayoutInflater.from(this@SentInbox).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.getSentInbox(userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {

                    val s = response.body()!!.string()
                    if (s != null) {

                        val jsonObject = JSONObject(s)

                        if (jsonObject.getBoolean("status")) {
                            val jsonArray = jsonObject.getJSONArray("data")

                            anim.visibility = View.GONE
                            listview.visibility = View.VISIBLE

                            if (jsonArray.length() > 0) {

                                for (i in 0 until jsonArray.length())
                                {
                                    val c = jsonArray.getJSONObject(i)

                                    val modelObject = InboxModel()


                                    modelObject.video = c.getString("video")
                                    // modelObject.vdo_url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                                    modelObject.postdate= c.getString("date")+" "+c.getString("time")
                                    modelObject.name = c.getString("to_name")
                                    modelObject.wishtype = c.getString("wish_type")
                                    modelObject.mobile = c.getString("to_mobile")
                                    modelObject.email = c.getString("to_email")
                                    modelObject.status = c.getString("delivery_status")

                                    Log.v("VIDEO URL",c.getString("video"))

                                    homeModel.add(modelObject)
                                }

                                set_bhk_adapter_list()

                            } else {

                                anim.visibility = View.VISIBLE
                                listview.visibility = View.GONE

                                progress_spinner.dismiss()
                                Toast.makeText(this@SentInbox, "Inbox Empty !", Toast.LENGTH_SHORT).show()
                            }

                            progress_spinner.dismiss()

                        } else {
                            anim.visibility = View.VISIBLE
                            listview.visibility = View.GONE
                            progress_spinner.dismiss()
                            Toast.makeText(this@SentInbox, " Inbox Empty!", Toast.LENGTH_SHORT).show()
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
            homeAdapter = SentInboxAdapter(this@SentInbox, R.layout.sent_inbox_layout, homeModel)
            listview.adapter = homeAdapter
            homeAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }

}
