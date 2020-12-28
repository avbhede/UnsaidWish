package com.av.uwish.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.av.uwish.Model.InboxModel
import com.av.uwish.R
import com.av.uwish.UserPost


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


import java.util.*

class InboxAdapter(internal var context: Context, resourse: Int, internal var hisModel: ArrayList<InboxModel>) : ArrayAdapter<InboxModel>(context, resourse, hisModel) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getCount(): Int {
        return hisModel.size
    }


    override fun getItem(position: Int): InboxModel? {
        return hisModel[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    inner class ViewHolder
    {

        lateinit var id:TextView
        lateinit var mobile: TextView
        lateinit var datetime: TextView
        lateinit var name:TextView
        lateinit var wishtype:TextView
        lateinit var exoPlayerView:SimpleExoPlayerView
        lateinit var sharedPreferences: SharedPreferences
        lateinit var cardInbox:CardView
        var exoPlayer: SimpleExoPlayer? = null

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder? = null
        val HistoryModel = getItem(position)
        if (convertView == null) {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.inbox_layout, null, false)
            holder = ViewHolder()

            holder.mobile = convertView.findViewById(R.id.mob)
            holder.wishtype = convertView.findViewById(R.id.wish_typetv)
            holder.datetime = convertView.findViewById(R.id.post_date)
            holder.name = convertView.findViewById(R.id.post_uname)
            holder.exoPlayerView = convertView.findViewById(R.id.v1)
            holder.cardInbox = convertView.findViewById(R.id.cardinbox)


            holder.mobile.text = HistoryModel!!.mobile.toString()
            holder.wishtype.text = HistoryModel!!.wishtype.toString()
            holder.datetime!!.text = HistoryModel.postdate
            holder.name.text = HistoryModel!!.name.toString()

            try {
                val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
                val trackSelector: TrackSelector = DefaultTrackSelector(
                    AdaptiveTrackSelection.Factory(bandwidthMeter)
                )
                holder.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

                val dataSourceFactory =DefaultHttpDataSourceFactory("exoplayer_video")
                val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
                val mediaSource: MediaSource = ExtractorMediaSource(
                    Uri.parse(HistoryModel!!.video),
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

            holder.cardInbox.setOnClickListener {

                var intent= Intent(context, UserPost::class.java)
                intent.putExtra("view", "true")
                intent.putExtra("video_url", HistoryModel!!.video)
                intent.putExtra("postdate", HistoryModel.postdate)
                intent.putExtra("wishname", HistoryModel.wishtype)
                context.startActivity(intent)

            }


        } else {
        }

        return convertView!!

    }

}
