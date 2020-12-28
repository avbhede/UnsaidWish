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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.av.uwish.Adapter.MyPostAdapter
import com.av.uwish.Model.MyPostModel
import com.av.uwish.Model.MyVideoModel
import com.av.uwish.Retrofit.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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

class ProfileFragment : Fragment() {

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 546
        private const val PICK_COVER_REQUEST_CODE = 547

    }

    lateinit var adapter: MyPostAdapter
    lateinit var alertDialog: android.app.AlertDialog

    lateinit var recyclerView: RecyclerView
    lateinit var videoModel: java.util.ArrayList<MyVideoModel>
    lateinit var mypostModel: java.util.ArrayList<MyPostModel>

    lateinit var profile_anim:LottieAnimationView
    lateinit var tv_uname:TextView
    lateinit var tv_mobile:TextView
    lateinit var tv_email:TextView
    lateinit var tv_followwers:TextView
    lateinit var tv_followwing:TextView
    lateinit var edit_img:ImageView
    lateinit var profile_img:CircleImageView
    lateinit var backProfile:ImageView
    lateinit var open_drawer:ImageView
    lateinit var edit_profile:Button
    private val uiHelper = UiHelper()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private var bitmap: Bitmap? = null
    lateinit var mobileStr:String
    lateinit var userid:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview = inflater.inflate(R.layout.fragment_profile, container, false)


        recyclerView = rootview.findViewById(R.id.mGridView)
        tv_email = rootview.findViewById(R.id.tv_email)
        tv_uname = rootview.findViewById(R.id.tv_uname)
        tv_mobile = rootview.findViewById(R.id.tv_mobile)
        edit_profile = rootview.findViewById(R.id.edit_profile)
        edit_img = rootview.findViewById(R.id.edit_profileimg)
        profile_img = rootview.findViewById(R.id.profile_img)
        open_drawer = rootview.findViewById(R.id.drawer)
        profile_anim = rootview.findViewById(R.id.animationView_profile)
        tv_followwers = rootview.findViewById(R.id.followers)
        tv_followwing = rootview.findViewById(R.id.followings)
        backProfile = rootview.findViewById(R.id.v_one_login)


        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()

        videoModel=ArrayList()
        mypostModel=ArrayList()

        tv_email.text = sharedPreferences.getString("email","").toString()
        tv_mobile.text = sharedPreferences.getString("mobile","").toString()
        tv_uname.text = sharedPreferences.getString("fname","").toString()+ " " + sharedPreferences.getString("lname","").toString()

        Log.v("PHOTO",sharedPreferences.getString("photo","").toString()+" JJJ")

        if(sharedPreferences.getString("photo","").toString() !="")
        {
            Picasso
                .get()
                .load(sharedPreferences.getString("photo","").toString())
                .into(profile_img)

        }
        else
        {
            profile_img.setImageResource(R.drawable.ic_person2)
        }

        getProfile()
        getMyPost()

        open_drawer.setOnClickListener {

            // main_obj.openDrawer()
            (getActivity() as MainActivity).openDrawer()

        }



        edit_img.setOnClickListener {

            // get prompts.xml view
            val li = LayoutInflater.from(context)
            val promptsView = li.inflate(R.layout.upload_prof_cover, null)
            val alertDialogBuilder = android.app.AlertDialog.Builder(context)

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView)

            val cover = promptsView.findViewById<TextView>(R.id.cover)
            val profile = promptsView.findViewById<TextView>(R.id.profile)


            profile.setOnClickListener {

                alertDialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE)

            }


            cover.setOnClickListener {

                alertDialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_COVER_REQUEST_CODE)

            }

            // create alert dialog
            alertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()

        }

        edit_profile.setOnClickListener {

            var intent=Intent(activity,EditProfile::class.java)
            startActivity(intent)

        }

        return rootview

    }

    /* fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener<Void?> {
                Toast.makeText(activity,"Successfully signed out",Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, login_signup::class.java))
            })
    }

     */


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
                        var mname=jsonObject.getString("mname")
                        var lname=jsonObject.getString("lname")
                        var email=jsonObject.getString("email")
                        var state:String=jsonObject.getString("state")
                        var city:String=jsonObject.getString("city")
                        var country:String=jsonObject.getString("country")
                        var password:String=jsonObject.getString("password")
                        var photo:String=jsonObject.getString("photo")

                        tv_followwing.text = jsonObject.getString("follow")
                        tv_followwers.text = jsonObject.getString("following")

                        Log.v("PHOTO_LOGIN",photo)

                        if(jsonObject.getString("cover") !="")
                        {
                            Picasso
                                .get()
                                .load(jsonObject.getString("cover"))
                                .into(backProfile)

                        }

                        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()


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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 546 && Activity.RESULT_OK == resultCode) {

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
        }else if (requestCode == 547 && Activity.RESULT_OK == resultCode) {

                data?.let {
                    try {

                        bitmap = uiHelper.decodeUri(activity!!.applicationContext, it.data!!)
                        backProfile.setImageBitmap(bitmap)

                        if (bitmap != null) {

                            backProfile.setImageBitmap(bitmap)
                            val imageBytes = uiHelper.getImageUrl(bitmap)
                            Log.v("IMG URL BYTES CODE",imageBytes)
                            uploadImageCover(imageBytes)

                        }

                    } catch (e: Exception) {

                        if (bitmap != null) {

                            backProfile.setImageBitmap(bitmap)
                            val imageBytes = uiHelper.getImageUrl(bitmap!!)
                            Log.v("IMG URL BYTES CODE",imageBytes)
                            uploadImageCover(imageBytes)

                        }

                    }
                }
            }
    }




    fun uploadImageCover(imageBytes: String)
    {
        val materialDialog = uiHelper.showAlwaysCircularProgress(context!!, "Cover Photo Updating...")
        ServiceApi.Factory.getInstance(context!!)?.uploadImageCover(imageBytes,userid)
            ?.enqueue(object : Callback<StatusMessageResponse> {
                override fun onFailure(call: Call<StatusMessageResponse>?, t: Throwable?) {
                    Toast.makeText(activity, "Cover Photo uploaded", Toast.LENGTH_SHORT).show()
                    materialDialog.dismiss()
                }

                override fun onResponse(call: Call<StatusMessageResponse>?, response: Response<StatusMessageResponse>?) {
                    materialDialog.dismiss()
                    // Error Occurred during uploading
                }
            })

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
                                    modelObject.userid = userid
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
