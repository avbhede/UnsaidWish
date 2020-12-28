package com.av.uwish.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.av.uwish.Model.MyPostModel
import com.av.uwish.R
import com.av.uwish.UserPost
import com.google.android.exoplayer2.SimpleExoPlayer


class  MyPostAdapter (internal var context: Context, private val mContacts: ArrayList<MyPostModel>) : RecyclerView.Adapter<MyPostAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {

        var exoPlayerView = itemView.findViewById<ImageView>(R.id.video_grid)
        var tv_wishname = itemView.findViewById<TextView>(R.id.wish_name)
        var tv_postdate = itemView.findViewById<TextView>(R.id.video_date)
        var tv_likes = itemView.findViewById<TextView>(R.id.likes)
        var tv_dislikes = itemView.findViewById<TextView>(R.id.dislikes)
        var cardview = itemView.findViewById<CardView>(R.id.cardviewid)
        var tv_report = itemView.findViewById<TextView>(R.id.report)

        var exoPlayer: SimpleExoPlayer? = null

    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.video_item_profile, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }


    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contact: MyPostModel = mContacts.get(position)

        holder.tv_wishname.text = contact.name
        holder.tv_postdate.text = contact.postdate
        holder.tv_likes.text = contact.likes
        holder.tv_dislikes.text = contact.dislikes


        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                contact.video,
                HashMap<String, String>()
            ) else mediaMetadataRetriever.setDataSource(contact.video)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime()

            holder.exoPlayerView.setImageBitmap(bitmap)


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release()
            }
        }


      /*  try {

            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val trackSelector: TrackSelector = DefaultTrackSelector(
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            )
            holder.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

            val dataSourceFactory =DefaultHttpDataSourceFactory("exoplayer_video")
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaSource: MediaSource = ExtractorMediaSource(
                Uri.parse(contact.video),
                dataSourceFactory,
                extractorsFactory,
                null,
                null
            )
            holder.exoPlayerView!!.setPlayer(holder.exoPlayer)
            holder.exoPlayer!!.prepare(mediaSource)
            holder.exoPlayer!!.setPlayWhenReady(false)
        } catch (e: Exception) {
            Log.e("MainAcvtivity", " exoplayer error $e")
        }

       */

        holder.cardview.setOnClickListener {

            val intent = Intent(context, UserPost::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.putExtra("likes_count", contact.likes)
            intent.putExtra("deslikes_count", contact.dislikes)
            intent.putExtra("video_url", contact.video)
            intent.putExtra("postdate", contact.postdate)
            intent.putExtra("wishname", contact.name)
            context.startActivity(intent)

        }


    }
}