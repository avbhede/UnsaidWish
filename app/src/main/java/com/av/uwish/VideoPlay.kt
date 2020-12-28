package com.av.uwish


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlay : AppCompatActivity() {

    lateinit var mvideoView:VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)

        mvideoView = findViewById(R.id.video_view)

        var videoUrl:Uri=Uri.parse(intent.getStringExtra("video_url"))
        mvideoView.setVideoURI(videoUrl)
        mvideoView.start()


    }


    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
        this.finish()
    }

}
