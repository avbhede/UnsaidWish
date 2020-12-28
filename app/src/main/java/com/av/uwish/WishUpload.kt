package com.av.uwish

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Contacts
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class WishUpload : AppCompatActivity() {


    lateinit var et_name:EditText
    lateinit var et_ddate:EditText
    lateinit var et_dtime:EditText
    lateinit var et_email:EditText
    lateinit var et_mobile:EditText
    lateinit var upload:Button
    lateinit var btn_gallery:Button
    lateinit var btn_shoot:Button
    lateinit var videoPlay:VideoView
    lateinit var wishTitle:TextView
    lateinit var back:ImageView

    lateinit var social_ly:LinearLayout
    lateinit var insta:EditText
    lateinit var linkedin:EditText
    lateinit var skype:EditText
    lateinit var facebook:EditText

    lateinit var selectedVideo: Uri
    lateinit var pathToStoredVideo:String
    lateinit var userid:String
    lateinit var wish_type:String
    lateinit var sharedPreferences: SharedPreferences
    private val uiHelper = UiHelper()

    var mYear:Int?=null
    var mMonth:Int?=null
    var mDay:Int?=null

    var mHour:Int?=null
    var mMinute:Int?=null
    var AMPM:String?=null

    companion object {
        private const val VIDEO_REQUEST = 1
        private const val REQUEST_VIDEO_CAPTURE = 300
        private const val READ_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_upload)

        et_name = findViewById(R.id.et_wishname)
        et_ddate = findViewById(R.id.et_wishDate)
        et_dtime = findViewById(R.id.et_wishTime)
        et_email = findViewById(R.id.et_wishEmail)
        et_mobile = findViewById(R.id.et_wishmobile)
        upload = findViewById(R.id.btn_uploadWish)
        btn_shoot = findViewById(R.id.btn_shoot_wish)
        btn_gallery = findViewById(R.id.btn_gallery_wish)
        wishTitle = findViewById(R.id.wishheading)
        back = findViewById(R.id.back_wish)
        videoPlay = findViewById(R.id.preview)


        social_ly = findViewById(R.id.social_ly)
        skype = findViewById(R.id.et_skype)
        facebook = findViewById(R.id.et_facebook)
        insta = findViewById(R.id.et_insta)
        linkedin = findViewById(R.id.et_linked)


        wish_type = intent.getStringExtra("wish_type")
        wishTitle.text = intent.getStringExtra("wish_type")

        if(wish_type.equals("Last Wish"))
        {
            social_ly.visibility = View.VISIBLE
        }

        val mediaController = MediaController(this@WishUpload)
        mediaController.setAnchorView(videoPlay)

        back.setOnClickListener {
            super.onBackPressed()
        }

        et_mobile.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, 3)

        }

        // Select Date
        et_ddate.setOnClickListener {


            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            var month:Int= mMonth!!

            val datePickerDialog = DatePickerDialog(this@WishUpload,
                object: DatePickerDialog.OnDateSetListener {

                    override fun onDateSet(view:DatePicker, year:Int,monthOfYear:Int, dayOfMonth:Int) {

                        var month=monthOfYear + 1
                        et_ddate.setText("$dayOfMonth-$month-$year")

                    }
                }, mYear!!, month, mDay!!)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

        }


        // Select Time
        et_dtime.setOnClickListener {

            // Get Current Time
            val c = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)


            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(this@WishUpload,
                object: TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view:TimePicker, hourOfDay:Int,minute:Int) {

                        et_dtime.setText(String.format("%02d:%02d",hourOfDay,minute))

                    }
                }, mHour!!, mMinute!!, true)

            timePickerDialog.show()

        }



        sharedPreferences =getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()

        btn_shoot.setOnClickListener {

            if(wishTitle.text == "Last Wish")
            {
                val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600)
                startActivityForResult(videoIntent, 1)
            }

            if(wishTitle.text == "Confession")
            {
                val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600)
                startActivityForResult(videoIntent, 1)
            }

            if(wishTitle.text == "Happy Moment")
            {
                val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 300)
                startActivityForResult(videoIntent, 1)
            }

            if(wishTitle.text == "Safety First")
            {
                val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                startActivityForResult(videoIntent, 1)
            }


        }

        btn_gallery.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, 2)

        }


        upload.setOnClickListener {

            if (et_name.text.equals("")) {

                et_name.setError("Name")
            }
            else if (et_ddate.text.equals("")) {

                et_ddate.setError("Enter Delivery Date")
            }
            else if (et_dtime.equals("")) {

                et_dtime.setError("Enter Delivery Time")
            }
            else if (et_mobile.equals("")) {

                et_mobile.setError("Enter Mobile")
            }
            else if (et_email.equals("")) {

                et_email.setError("Enter Email")
            }
            else if (pathToStoredVideo.equals("")) {

                Toast.makeText(this@WishUpload, "Select or Capture Video !", Toast.LENGTH_LONG).show()
            }
            else
            {
                uploadVideoToServer(pathToStoredVideo)
            }


        }

    }



    private fun uploadVideoToServer(pathToVideoFile:String) {

        val videoFile: File = File(pathToVideoFile)
        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody)


        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(1000, TimeUnit.SECONDS)
            .connectTimeout(1000, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://admin.unsaidwish.com/uploads/wish/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val materialDialog = uiHelper.showAlwaysCircularProgress(this@WishUpload, "Uploading...")

        val vInterface = retrofit.create(ApiConfig::class.java)
        val serverCom = vInterface.uploadWishToServer(vFile)
        serverCom.enqueue(object: Callback<ServerResponse> {

            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                val result = response.body()
                if (!TextUtils.isEmpty(result!!.success))
                {
                    Toast.makeText(this@WishUpload, "Result " + result!!.success, Toast.LENGTH_LONG).show()
                    Log.d("RESPONSE_API", "Result " + result.success)

                    var intent=Intent(this@WishUpload,MainActivity::class.java)
                    startActivity(intent)

                    postVideoGlobalId()

                    materialDialog.dismiss()
                }
                materialDialog.dismiss()
            }
            override fun onFailure(call: Call<ServerResponse>, t:Throwable) {
                Log.d("RESPONSE_API", "Error message " + t.message)
                materialDialog.dismiss()
            }
        })
    }


    fun postVideoGlobalId()
    {

        val call = RetrofitClient.instance.api.uploadWishId(userid,pathToStoredVideo,et_ddate.text.toString(),et_dtime.text.toString(),et_mobile.text.toString(),et_email.text.toString(),et_name.text.toString(),wish_type,
            facebook.text.toString(),insta.text.toString(),linkedin.text.toString(),skype.text.toString()
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        Log.v("STATUS","true")
                        Toast.makeText(this@WishUpload, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                    }
                    else {
                        Log.v("STATUS","false")

                        Toast.makeText(this@WishUpload, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Activity.RESULT_OK == resultCode) {

            if (requestCode == 1)
            {
                selectedVideo = data!!.data!!
                videoPlay.setVideoURI(selectedVideo)
                videoPlay.start()

                if(EasyPermissions.hasPermissions(this@WishUpload, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    pathToStoredVideo = getRealPathFromURIPath(selectedVideo, this@WishUpload!!)
                    Log.d("PATH ", "Recorded Video Path " + pathToStoredVideo)
                }else{
                    EasyPermissions.requestPermissions(this@WishUpload, getString(R.string.read_file),
                        WishUpload.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }else if(requestCode == 2)
            {

                selectedVideo = data!!.data!!
                videoPlay.setVideoURI(selectedVideo)
                videoPlay.start()

                val filePath = arrayOf(MediaStore.Video.Media.DATA)
                val c: Cursor = getContentResolver().query(selectedVideo, filePath,null, null, null)!!
                c.moveToFirst()
                val columnIndex: Int = c.getColumnIndex(filePath[0])
                pathToStoredVideo = c.getString(columnIndex)
                c.close()
                Log.d("SelectedVideoPath", pathToStoredVideo)


            }
            else if(requestCode == 3)
            {

                if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
                    var cursor: Cursor? = null
                    try {
                        val uri = data!!.data
                        cursor = contentResolver.query(uri!!,arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),null,null,null)
                        if (cursor != null && cursor.moveToNext()) {
                            val phone = cursor.getString(0)

                            et_mobile.setText(cursor.getString(0))

                            // Do something with phone
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }



        }
    }


    private fun getRealPathFromURIPath(contentURI:Uri, activity: Context):String {

        val cursor = activity.getContentResolver().query(contentURI, null, null, null, null)
        if (cursor == null){
            return contentURI.getPath().toString()
        }
        else{
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
