package com.av.uwish


import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.av.uwish.Adapter.UserMessInbox
import com.av.uwish.Model.InboxModel
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserInbox : Fragment() {

    lateinit var back:ImageView
    lateinit var listview:ListView
    lateinit var anim:LottieAnimationView
    lateinit var homeModel:java.util.ArrayList<InboxModel>
    lateinit var homeAdapter: UserMessInbox
    lateinit var progress_spinner: Dialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userid:String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {


        var view = inflater.inflate(R.layout.fragment_user_inbox, container, false)

        listview = view.findViewById(R.id.user_inbox_list)
        anim = view.findViewById(R.id.animationView_userinbox)


        sharedPreferences = context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()

        homeModel= ArrayList()


        getUserList()


        listview.setOnItemClickListener(object: AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view:View, position:Int,
                                     id:Long)
            {

                var mid:String=(view.findViewById(R.id.mid) as TextView).text.toString()

                var intent= Intent(context,UsersMessage::class.java)
                intent.putExtra("mid", mid)
                startActivity(intent)

            }
        })



        return view
    }



    fun getUserList()
    {
        progress_spinner = Dialog(context!!, android.R.style.Theme_Black)
        val view = LayoutInflater.from(context).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.getMessageUserList(userid)
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

                                    modelObject.name = c.getString("name")
                                    modelObject.id = c.getString("mid")
                                    modelObject.img_url = c.getString("img")

                                    homeModel.add(modelObject)
                                }

                                set_bhk_adapter_list()

                            } else {

                                anim.visibility = View.VISIBLE
                                listview.visibility = View.GONE

                                progress_spinner.dismiss()
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            }

                            progress_spinner.dismiss()

                        } else {

                            anim.visibility = View.VISIBLE
                            listview.visibility = View.GONE

                            progress_spinner.dismiss()
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
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
            homeAdapter = UserMessInbox(context!!, R.layout.user_inbox_layout, homeModel)
            listview.adapter = homeAdapter
            homeAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }



}