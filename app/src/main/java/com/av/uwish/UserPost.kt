package com.av.uwish

import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import de.hdodenhof.circleimageview.CircleImageView

class UserPost : AppCompatActivity() {


    lateinit var like_count: TextView
    lateinit var deslike_count: TextView
    lateinit var like: ImageView
    lateinit var deslike: ImageView
    lateinit var exoPlayerView: SimpleExoPlayerView
    lateinit var post_uname: TextView
    lateinit var post_date: TextView
    lateinit var post_prof: CircleImageView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userid:String
    lateinit var video_url:String
    var exoPlayer: SimpleExoPlayer? = null

    lateinit var like_layout:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)

        like_count = findViewById(R.id.like_count)
        deslike_count = findViewById(R.id.deslike_count)
        exoPlayerView = findViewById(R.id.v1)
        post_uname = findViewById(R.id.post_uname)
        post_date = findViewById(R.id.post_date)
        post_prof = findViewById(R.id.post_profile)
        like = findViewById(R.id.like)
        deslike = findViewById(R.id.deslike)

        like_layout = findViewById(R.id.like_layout)



        if(intent.getStringExtra("view")=="true")
        {
            like_layout.visibility = View.GONE
            video_url = intent.getStringExtra("video_url")
            post_date.text = intent.getStringExtra("postdate")
            post_uname.text = intent.getStringExtra("wishname")

        }
        else
        {
            like_count.text = intent.getStringExtra("likes_count")
            deslike_count.text = intent.getStringExtra("deslikes_count")
            video_url = intent.getStringExtra("video_url")
            post_date.text = intent.getStringExtra("postdate")
            post_uname.text = intent.getStringExtra("wishname")

        }


        try {

            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            )
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this@UserPost, trackSelector)

            val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaSource: MediaSource = ExtractorMediaSource(
                Uri.parse(video_url),
                dataSourceFactory,
                extractorsFactory,
                null,
                null
            )
            exoPlayerView!!.setPlayer(exoPlayer)
            exoPlayer!!.prepare(mediaSource)
            exoPlayer!!.setPlayWhenReady(false)
        } catch (e: Exception) {
            Log.e("MainAcvtivity", " exoplayer error $e")
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}
