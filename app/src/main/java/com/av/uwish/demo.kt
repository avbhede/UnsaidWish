package com.av.uwish
/*

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="right"
        android:visibility="gone"
        android:fitsSystemWindows="true"
        app:menu="@menu/menu"
        app:headerLayout="@layout/nav_header_main"/>


import android.util.Log
import android.widget.Toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Uploading Image/Video
/* private fun uploadFile() {

     val materialDialog = uiHelper.showAlwaysCircularProgress(context!!, "Video Post Uploading...")
     // Map is used to multipart the file using okhttp3.RequestBody
     val file = File(mediaPath1)

     // Parsing any Media type file
     val requestBody: RequestBody = RequestBody.create(MediaType.parse("*/*"), file)
val fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody)
val filename: RequestBody = RequestBody.create(MediaType.parse("text/plain"), file.getName())
val getResponse = AppConfig.retrofit.create(ApiConfig::class.java)

val call: Call<ResponseBody> = getResponse.uploadFile(fileToUpload, filename,userid)

call.enqueue(object : Callback<ResponseBody?> {

    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>)
    {
        val serverResponse = response.body().toString()

        Log.v("RESPONSE VIDOupload",serverResponse+" null ???")

        val jsonObject = JSONObject(serverResponse)

        if (serverResponse != null) {

            if (jsonObject.getBoolean("status")) {

                materialDialog.dismiss()
                Toast.makeText(activity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

            } else {

                materialDialog.dismiss()
                Toast.makeText(activity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

            }
        } else {
            materialDialog.dismiss()
            assert(serverResponse != null)
            Log.v("Response", serverResponse)
        }

    }

    override fun onFailure(
        call: Call<ResponseBody?>,
        t: Throwable
    ) {
    }
})
  <com.google.android.material.bottomnavigation.BottomNavigationView
            android:id="@+id/navigation"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            android:layout_gravity="bottom"
            android:background="#00FFFFFF"
            app:itemIconTint="@drawable/nav_button_color"
            app:itemTextColor="@color/colorAccent"
            app:menu="@menu/menu_bottom_navigation_basic"
            app:rippleColor="@color/colorAccent"/>

}*/