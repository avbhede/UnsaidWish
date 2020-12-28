package com.av.uwish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.av.uwish.Adapter.MyPostAdapter
import com.av.uwish.Model.MyPostModel
import com.av.uwish.Model.MyVideoModel
import com.av.uwish.Retrofit.RetrofitClient
import com.nix.namastestore.StatusMessageResponse
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
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
class User_fragment : Fragment() {

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 546
    }

    lateinit var adapter: MyPostAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var videoModel: java.util.ArrayList<MyVideoModel>
    lateinit var mypostModel: java.util.ArrayList<MyPostModel>


    lateinit var profile_anim:LottieAnimationView
    lateinit var tv_uname:TextView
    lateinit var tv_mobile:TextView
    lateinit var tv_email:TextView
    lateinit var vuname:TextView
    lateinit var back:ImageView
    lateinit var edit_img:ImageView
    lateinit var profile_img:CircleImageView
    lateinit var logout:ImageView
    lateinit var edit_profile:TextView
    private val uiHelper = UiHelper()
    lateinit var sharedPreferences: SharedPreferences

    lateinit var follow:TextView
    lateinit var message:TextView

    lateinit var followers:TextView
    lateinit var following:TextView
    lateinit var cover_photo:ImageView

    private var bitmap: Bitmap? = null
    lateinit var mobileStr:String
    lateinit var userid:String
    lateinit var c_uid:String
    lateinit var follow_status:String
     var follow_count:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview = inflater.inflate(R.layout.fragment_user_fragment, container, false)


        recyclerView = rootview.findViewById(R.id.mGridView)
        tv_email = rootview.findViewById(R.id.tv_email)
        tv_uname = rootview.findViewById(R.id.tv_uname)
        tv_mobile = rootview.findViewById(R.id.tv_mobile)
        edit_profile = rootview.findViewById(R.id.edit_profile)
        edit_img = rootview.findViewById(R.id.edit_profileimg)
        profile_img = rootview.findViewById(R.id.profile_img)
        logout = rootview.findViewById(R.id.log_out)
        profile_anim = rootview.findViewById(R.id.animationView_profile)
        vuname = rootview.findViewById(R.id.visit_uname)
        back = rootview.findViewById(R.id.back_profile)

        follow = rootview.findViewById(R.id.follow_user)
        message = rootview.findViewById(R.id.message_user)
        cover_photo = rootview.findViewById(R.id.cover_user)

        followers = rootview.findViewById(R.id.followers)
        following = rootview.findViewById(R.id.followings)


        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        c_uid = sharedPreferences.getString("id","").toString()

        sharedPreferences =context!!.getSharedPreferences("VISIT", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("uid","").toString()
        vuname.text = sharedPreferences.getString("uname","").toString()
        follow_status = sharedPreferences.getString("follow_status","").toString()


        if(follow_status.equals("true"))
        {
            follow.text = "Following"
        }

        Log.v("USERID",userid + "_share ")

        videoModel=ArrayList()
        mypostModel=ArrayList()

        getProfile()
        getMyPost()


        back.setOnClickListener(View.OnClickListener { v ->

            val activity = v.context as AppCompatActivity
            val myFragment: Fragment = HomeFragment()
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.maincontent, myFragment)
                .replace(R.id.maincontent, myFragment).addToBackStack(null)
                .commit()
        })


        sharedPreferences =context!!.getSharedPreferences("VISIT_USER_DATA", Context.MODE_PRIVATE)

        follow.setOnClickListener {

            followUser()

        }

        message.setOnClickListener {

            var intent=Intent(activity,UsersMessage::class.java)
            intent.putExtra("mid",userid)
            intent.putExtra("name",tv_uname.text )
            startActivity(intent)

        }


        logout.setOnClickListener {

            logoutalert()

        }

        edit_img.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE)
        }

        edit_profile.setOnClickListener {

            var intent=Intent(activity,EditProfile::class.java)
            startActivity(intent)

        }


        return rootview

    }

    fun followUser()
    {

        val call = RetrofitClient.instance.api.follow(c_uid,userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        if(follow.text.equals("Follow"))
                        {
                            Log.v("Response_Follow",jsonObject.getString("message"))
                            follow.text="Following"
                            followers.text = (follow_count + 1).toString()
                            follow_count = (follow_count + 1)

                        }else
                        {
                            Log.v("Response_Follow",jsonObject.getString("message"))
                            follow.text="Follow"
                            followers.text = (follow_count - 1).toString()
                            follow_count = (follow_count - 1)

                        }


                    }
                    else {
                        Log.v("Response_Follow",jsonObject.getString("message"))

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


    fun logoutalert() {

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Logout?")
        builder.setPositiveButton("OK") { dialog, id ->
            val sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
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

            var intent= Intent(activity, login_signup::class.java)
            startActivity(intent)


        }

        builder.show()
    }

    // Get UPDATED Profile

    fun getProfile() {

        val call = RetrofitClient.instance.api.getProfile(userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    val jsonObject = JSONObject(s)

                    // Log.v("STATUS "," "+jsonObject.getBoolean("status"))

                    if (jsonObject.getBoolean("status")) {


                        //var id:String=jsonObject.getString("id")
                        var fname=jsonObject.getString("fname")
                        //var uname=jsonObject.getString("uname")
                        var mname=jsonObject.getString("mname")
                        var lname=jsonObject.getString("lname")
                        var email=jsonObject.getString("email")
                        var state:String=jsonObject.getString("state")
                        var city:String=jsonObject.getString("city")
                        var country:String=jsonObject.getString("country")
                        var password:String=jsonObject.getString("password")
                        var photo:String=jsonObject.getString("photo")


                        Log.v("PHOTO_LOGIN",photo)

                        tv_email.text = email
                        tv_mobile.text = sharedPreferences.getString("mobile","").toString()
                        tv_uname.text = ("$fname $lname")

                        following.text = jsonObject.getString("follow")
                        followers.text = jsonObject.getString("following")
                        follow_count = jsonObject.getString("following").toInt()



                        if(jsonObject.getString("cover") != "")
                        {
                            Picasso
                                .get()
                                .load(jsonObject.getString("cover"))
                                .into(cover_photo)

                        }

                        if(photo!="")
                        {
                            Picasso
                                .get()
                                .load(photo)
                                .into(profile_img)

                        }
                        else
                        {
                            profile_img.setImageResource(R.drawable.ic_person2)
                        }

                        sharedPreferences =context!!.getSharedPreferences("VISIT_USER_DATA", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()


                        //editor.putString("uname", uname)
                        editor.putString("fname", fname)
                        editor.putString("mname", mname)
                        editor.putString("lname", lname)
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.putString("state", state)
                        editor.putString("city", city)
                        editor.putString("country", country)
                        editor.putString("photo", photo)
                        editor.commit()



                    } else {

                        profile_anim.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE

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
                        editor.putBoolean("isLoggedIn", true)

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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && Activity.RESULT_OK == resultCode) {

            data?.let {
                try {

                    bitmap = uiHelper.decodeUri(activity!!.applicationContext, it.data!!)
                    profile_img.setImageBitmap(bitmap)

                    if (bitmap != null) {

                        profile_img.setImageBitmap(bitmap)
                        val imageBytes = uiHelper.getImageUrl(bitmap)
                        Log.v("IMG URL BYTES CODE",imageBytes)
                        uploadImage(imageBytes)

                    }

                } catch (e: Exception) {

                    if (bitmap != null) {

                        profile_img.setImageBitmap(bitmap)
                        val imageBytes = uiHelper.getImageUrl(bitmap!!)
                        Log.v("IMG URL BYTES CODE",imageBytes)
                        uploadImage(imageBytes)

                    }

                }
            }
        }
    }


    fun uploadImage(imageBytes: String)
    {
        val materialDialog = uiHelper.showAlwaysCircularProgress(context!!, "Profile Updating...")
        ServiceApi.Factory.getInstance(context!!)?.uploadImage(imageBytes,userid)
            ?.enqueue(object : Callback<StatusMessageResponse> {
                override fun onFailure(call: Call<StatusMessageResponse>?, t: Throwable?) {
                    Toast.makeText(activity, "Profile uploaded", Toast.LENGTH_SHORT).show()
                    materialDialog.dismiss()
                }

                override fun onResponse(call: Call<StatusMessageResponse>?, response: Response<StatusMessageResponse>?) {
                    materialDialog.dismiss()
                    // Error Occurred during uploading
                }
            })

    }




    fun getMyPost()
    {

        val call = RetrofitClient.instance.api.getMyPost(userid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val s = response.body()!!.string()
                    if (s != null) {

                        val jsonObject = JSONObject(s)

                        if (jsonObject.getBoolean("status")) {
                            val jsonArray = jsonObject.getJSONArray("data")


                            profile_anim.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            if (jsonArray.length() > 0) {

                                for (i in 0 until jsonArray.length())
                                {
                                    val c = jsonArray.getJSONObject(i)

                                    val modelObject = MyPostModel()


                                    modelObject.video = c.getString("video")
                                    modelObject.postdate= c.getString("postdate")
                                    modelObject.name = c.getString("name")
                                    // modelObject.wishname = c.getString("likes")
                                    modelObject.likes = c.getString("likes")
                                    modelObject.dislikes = c.getString("dislikes")
                                    modelObject.user_response = c.getString("user_response")


                                    Log.v("VIDEO URL",c.getString("video"))

                                    mypostModel.add(modelObject)
                                }

                                set_bhk_adapter_list()

                            } else {

                                profile_anim.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE

                                Toast.makeText(activity, "Post not available !", Toast.LENGTH_SHORT).show()
                            }

                        } else {

                            profile_anim.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE

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

            val adapter = MyPostAdapter(context!!,mypostModel)
            recyclerView.adapter = adapter

            val manager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = manager

        } catch (e: NullPointerException) {
        }

    }



}
