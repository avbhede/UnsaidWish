package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.av.uwish.Adapter.InboxAdapter
import com.av.uwish.Model.InboxModel
import com.av.uwish.Retrofit.RetrofitClient
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
class InboxFragment : Fragment() {

    lateinit var homeModel:java.util.ArrayList<InboxModel>
    lateinit var homeAdapter:InboxAdapter
    lateinit var listview: ListView
    lateinit var progress_spinner: Dialog
    lateinit var anim: LottieAnimationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userid:String
    lateinit var sentinbox:TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview= inflater.inflate(R.layout.fragment_inbox, container, false)

        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()

        listview = rootview.findViewById(R.id.inbox_list)
        sentinbox = rootview.findViewById(R.id.sent_inbox)
        anim = rootview.findViewById(R.id.animationView_inbox)

        homeModel= ArrayList()


        sentinbox.setOnClickListener {

            var intent= Intent(context,SentInbox::class.java)
            startActivity(intent)

        }

        getHomeVideo()


        listview.setOnItemClickListener(object: AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view:View, position:Int,
                                     id:Long)
            {

                var title:String=(view.findViewById(R.id.post_uname) as TextView).text.toString()
                var link:String=(view.findViewById(R.id.videolink) as TextView).text.toString()
                var date:String=(view.findViewById(R.id.post_date) as TextView).text.toString()
                var type:String=(view.findViewById(R.id.wish_typetv) as TextView).text.toString()

                var intent= Intent(context,UserPost::class.java)
                intent.putExtra("view", "true")
                intent.putExtra("video_url", link)
                intent.putExtra("postdate", date)
                intent.putExtra("wishname", type)
                startActivity(intent)

            }
        })

        return rootview
    }



    fun getHomeVideo()
    {
        progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
        val view = LayoutInflater.from(activity).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.getInbox(userid)
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
                                    modelObject.name = c.getString("from_name")
                                    modelObject.wishtype = c.getString("wish_type")
                                    modelObject.mobile = c.getString("from_mobile")


                                    Log.v("VIDEO URL",c.getString("video"))

                                    homeModel.add(modelObject)
                                }

                                set_bhk_adapter_list()

                            } else {

                                anim.visibility = View.VISIBLE
                                listview.visibility = View.GONE

                                progress_spinner.dismiss()
                                Toast.makeText(activity, "Post not available !", Toast.LENGTH_SHORT).show()
                            }

                            progress_spinner.dismiss()

                        } else {

                            anim.visibility = View.VISIBLE
                            listview.visibility = View.GONE

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
            homeAdapter = InboxAdapter(context!!, R.layout.inbox_layout, homeModel)
            listview.adapter = homeAdapter
            homeAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }


}
