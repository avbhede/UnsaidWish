package com.av.uwish


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.av.uwish.Retrofit.RetrofitClient
import com.google.android.exoplayer2.SimpleExoPlayer
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
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */

class Camera : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val VIDEO_REQUEST = 1
        private const val REQUEST_VIDEO_CAPTURE = 300
        private const val READ_REQUEST_CODE = 200
    }

    lateinit var selectedVideo:Uri
    lateinit var upload_wish:Button

    var exoPlayerView: VideoView? = null
    var exoPlayer: SimpleExoPlayer? = null
    lateinit var mediaPath1:String
    lateinit var userid:String
    lateinit var remove:TextView
    lateinit var pathToStoredVideo:String
    lateinit var gallery:Button
    lateinit var camera:Button
    lateinit var videoBytes:ByteArray
    private val uiHelper = UiHelper()
    var mediaColumns = arrayOf(MediaStore.Video.Media._ID)
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var rootview= inflater.inflate(R.layout.fragment_camera, container, false)


        upload_wish = rootview.findViewById(R.id.btn_uploadGlobal)
        exoPlayerView = rootview.findViewById(R.id.preview)
        camera = rootview.findViewById(R.id.btn_shoot)
        gallery = rootview.findViewById(R.id.btn_gallery)

        sharedPreferences =context!!.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("id","").toString()


        camera.setOnClickListener {

            val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            //videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            startActivityForResult(videoIntent, 1)

        }

        gallery.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, 2)

        }

        upload_wish.setOnClickListener {

            uploadVideoToServer(pathToStoredVideo)

        }

        return rootview

     }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (RESULT_OK == resultCode) {

            if (requestCode == 1)
            {
                    selectedVideo = data!!.data!!

             /*   try {
                    
                    val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
                    val trackSelector: TrackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)

                    val dataSourceFactory =DefaultHttpDataSourceFactory("exoplayer_video")
                    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
                    val mediaSource: MediaSource = ExtractorMediaSource(
                        selectedVideo,
                        dataSourceFactory,
                        extractorsFactory,
                        null,
                        null
                    )

                    exoPlayerView!!.setPlayer(exoPlayer)
                    exoPlayer!!.prepare(mediaSource)

                } catch (e: Exception) {
                    Log.e("MainAcvtivity", " exoplayer error $e")
                }

              */

                    exoPlayerView!!.setVideoURI(selectedVideo)
                    exoPlayerView!!.start()

                    upload_wish.visibility=View.VISIBLE

                    if(EasyPermissions.hasPermissions(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                        pathToStoredVideo = getRealPathFromURIPath(selectedVideo, context!!)
                        Log.d("PATH ", "Recorded Video Path " + pathToStoredVideo+" "+selectedVideo)
                    }else{
                        EasyPermissions.requestPermissions(context, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

            }
            else if(requestCode == 2)
            {

                     selectedVideo = data!!.data!!
                     exoPlayerView!!.setVideoURI(selectedVideo)
                     exoPlayerView!!.start()

                    val filePath = arrayOf(MediaStore.Video.Media.DATA)
                    val c: Cursor = context!!.getContentResolver().query(selectedVideo!!, filePath,null, null, null)!!
                    c.moveToFirst()
                    val columnIndex: Int = c.getColumnIndex(filePath[0])
                    pathToStoredVideo = c.getString(columnIndex)
                    c.close()
                    Log.d("SelectedVideoPath", pathToStoredVideo)
                    Log.d("PATH ", "Recorded Video Path " + pathToStoredVideo+" "+selectedVideo)

                    upload_wish.visibility=View.VISIBLE

            }

        }
    }


    private fun uploadVideoToServer(pathToVideoFile:String) {

            val videoFile:File = File(pathToVideoFile)
            val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
            val vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody)


            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.SECONDS)
                .connectTimeout(1000, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://admin.unsaidwish.com/uploads/global/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            val materialDialog = uiHelper.showAlwaysCircularProgress(context!!, "Uploading...")

            val vInterface = retrofit.create(ApiConfig::class.java)
            val serverCom = vInterface.uploadVideoToServer(vFile)
            serverCom.enqueue(object: Callback<ServerResponse> {

                override fun onResponse(call:Call<ServerResponse>, response:Response<ServerResponse>) {
                    val result = response.body()
                    if (!TextUtils.isEmpty(result!!.success))
                    {
                        Toast.makeText(activity, "Result " + result!!.success, Toast.LENGTH_LONG).show()
                        Log.d("RESPONSE_API", "Result " + result.success)

                        var intent=Intent(activity,MainActivity::class.java)
                        startActivity(intent)

                        Log.v("Upload Path",pathToVideoFile+" "+pathToStoredVideo)

                        postVideoGlobalId()

                        materialDialog.dismiss()
                    }
                    materialDialog.dismiss()
                }
                override fun onFailure(call:Call<ServerResponse>, t:Throwable) {
                    Log.d("RESPONSE_API", "Error message " + t.message)
                    materialDialog.dismiss()
                }
            })
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

    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, context)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>?) {
        if (selectedVideo != null) {
            if (EasyPermissions.hasPermissions(context,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                pathToStoredVideo = getRealPathFromURIPath(selectedVideo!!, context!!)
                Log.d("TAG", "Recorded Video Path $pathToStoredVideo")
                //Store the video to your server
                uploadVideoToServer(pathToStoredVideo)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>?) {
        Log.d("TAG", "User has denied requested permission")
    }

    fun postVideoGlobalId()
    {

        val call = RetrofitClient.instance.api.uploadId(userid,pathToStoredVideo)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    val s = response.body()!!.string()

                    val jsonObject = JSONObject(s)

                    Log.v("STATUS",jsonObject.getBoolean("status").toString())

                    if (jsonObject.getBoolean("status")) {

                        Log.v("STATUS","true")
                        //Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                    }
                    else {
                        Log.v("STATUS","false")

                       //Toast.makeText(activity, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()
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
