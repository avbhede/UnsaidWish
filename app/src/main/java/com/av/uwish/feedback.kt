package com.av.uwish

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.av.uwish.Adapter.ReviewAdapter
import com.av.uwish.Model.ReviewModel
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class feedback : AppCompatActivity() {

    lateinit var listview:ListView
    lateinit var back:ImageView
    lateinit var hisModel: java.util.ArrayList<ReviewModel>
    lateinit var hisAdapter: ReviewAdapter
    lateinit var his_listView: ListView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var mobile:String
    lateinit var uname:String
    lateinit var alertDialog: android.app.AlertDialog
    lateinit var progress_spinner: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        listview=findViewById(R.id.review_listview)
        back=findViewById(R.id.backorder)

        back.setOnClickListener {
            super.onBackPressed()
        }

        var fabFeedback=findViewById(R.id.fabFeedback) as FloatingActionButton

        hisModel= ArrayList()

        getReview()

        fabFeedback.setOnClickListener {

            sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            mobile = sharedPreferences.getString("mobile","").toString()
            uname = sharedPreferences.getString("username","").toString()

            feedbackAlert()

        }


    }



    private fun getReview()
    {
        progress_spinner = Dialog(this@feedback, android.R.style.Theme_Black)
        val view = LayoutInflater.from(this@feedback).inflate(R.layout.aux_progress_spinner, null)
        progress_spinner.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progress_spinner.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        progress_spinner.setContentView(view)
        progress_spinner.show()

        val call = RetrofitClient.instance.api.showfeedback("")
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
                                    val modelObject = ReviewModel()

                                    modelObject.rev_name = c.getString("name")
                                    modelObject.rev_date=c.getString("date")
                                    modelObject.rev_mesg=c.getString("message")
                                    modelObject.rev_rating=c.getString("rating")

                                    hisModel.add(modelObject)
                                }

                                set_bhk_adapter_list()

                            } else {
                                Toast.makeText(this@feedback, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            }

                            progress_spinner.dismiss()

                        } else {
                            progress_spinner.dismiss()
                            Toast.makeText(this@feedback, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
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
            hisAdapter = ReviewAdapter(this@feedback, R.layout.review_layout, hisModel)
            listview.adapter = hisAdapter
            hisAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }


    private fun feedbackAlert()
    {
        // get prompts.xml view
        val li = LayoutInflater.from(this@feedback)
        val promptsView = li.inflate(R.layout.feedback, null)
        val alertDialogBuilder = android.app.AlertDialog.Builder(this@feedback)

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        alertDialogBuilder.setCancelable(false)


        val cancel = promptsView.findViewById<Button>(R.id.cancel)
        val submit = promptsView.findViewById<Button>(R.id.submit)
        val rating = promptsView.findViewById<RatingBar>(R.id.RevRating)
        var et_message=promptsView.findViewById<EditText>(R.id.feedback_msg)

        cancel.setOnClickListener { alertDialog.cancel() }


        submit.setOnClickListener {

            val message = et_message.text.toString()
            val rat=rating.rating


            if(message=="")
            {

                et_message.error="Enter Comments"
            }
            else{

                sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                mobile=sharedPreferences.getString("id","").toString()

                val call = RetrofitClient.instance.api.send_feedback(mobile,message,rat.toString())
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
                            val s = response.body()!!.string()
                            if (s != null)
                            {
                                val jsonObject = JSONObject(s)

                                if (jsonObject.getBoolean("status")) {


                                    Toast.makeText(this@feedback, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                                    alertDialog.cancel()
                                    hisModel.clear()
                                    getReview()

                                }
                                else{

                                    Toast.makeText(this@feedback, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

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


        }

        // create alert dialog
        alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }



}
