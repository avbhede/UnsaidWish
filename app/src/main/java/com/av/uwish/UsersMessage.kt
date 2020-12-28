package com.av.uwish

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.av.uwish.Adapter.GroupChatMsgAdapter
import com.av.uwish.Model.ChatModel
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

class UsersMessage : AppCompatActivity() {

    lateinit var mid:String
    lateinit var groupChatModel: ArrayList<ChatModel>
    lateinit var groupChatMsgAdapter: GroupChatMsgAdapter
    lateinit var listview: ListView
    lateinit var uname_tv:TextView

    lateinit var btn_send: Button
    lateinit var back:ImageView
    lateinit var et_msg: EditText
    lateinit var msg:String
    lateinit var userid:String
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_message)

        btn_send=findViewById(R.id.send_msg)
        et_msg=findViewById(R.id.et_msg)
        uname_tv = findViewById(R.id.chat_name)
        listview = findViewById(R.id.chat_listview)
        back = findViewById(R.id.chat_back)

        sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()

        mid = intent.getStringExtra("mid")
        uname_tv.text= intent.getStringExtra("name")

        groupChatModel = ArrayList()

        getChat()

        back.setOnClickListener {
            super.onBackPressed()
        }

        btn_send.setOnClickListener {

            msg=et_msg.text.toString()
                et_msg.text=null
                add_post_desc()
        }

     /*   try {
            //getChat()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {


                   // getChat()
                   // groupChatModel.clear()
                    groupChatMsgAdapter.notifyDataSetChanged()
                    handler.postDelayed(this, 1000.toLong())
                }
            }, 3000.toLong())
        } catch (e: java.lang.IndexOutOfBoundsException) {

            Log.v("EXCEPTION",e.message)
            // display("Network error.\nPlease check with your network settings.")
        }

      */


    }



     fun getChat() {

        val call = RetrofitClient.instance.api.get_chats(mid,userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    if (s != null) {

                        val jsonObject = JSONObject(s)

                        Log.v("STATUS GP_MEM",jsonObject.getBoolean("status").toString())

                        if (jsonObject.getBoolean("status")) {

                            val jsonArray = jsonObject.getJSONArray("data")

                            if (jsonArray.length() > 0) {

                                for (i in 0 until jsonArray.length()) {
                                    val c = jsonArray.getJSONObject(i)
                                    val modelObject = ChatModel()

                                    modelObject.mem_mob = mid
                                    modelObject.message = c.getString("message")
                                    modelObject.msg_time = c.getString("date")+" "+c.getString("time")

                                    groupChatModel.add(modelObject)

                                }

                                get_chat_adapter_list()

                            } else {

                                Toast.makeText(this@UsersMessage, "No Messages", Toast.LENGTH_SHORT).show()
                            }

                        } else {

                            Toast.makeText(this@UsersMessage, "No Messages", Toast.LENGTH_SHORT).show()
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


    fun get_chat_adapter_list() {

        try {

            groupChatMsgAdapter = GroupChatMsgAdapter(this@UsersMessage, R.layout.chat_msg_layout, groupChatModel)
            listview.adapter = groupChatMsgAdapter
            groupChatMsgAdapter.notifyDataSetChanged()

        } catch (e: NullPointerException) {
        }

    }



    fun add_post_desc(){

        val call = RetrofitClient.instance.api.send_chat_desc(msg,mid,userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val s = response.body()!!.string()
                    if (s != null) {
                        val jsonObject = JSONObject(s)

                        if (jsonObject.getBoolean("status")) {

                            groupChatModel.clear()
                            groupChatMsgAdapter.notifyDataSetChanged()
                        }
                        else{

                            Toast.makeText(this@UsersMessage, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
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


    override fun onBackPressed() {
        super.onBackPressed()
    }

}