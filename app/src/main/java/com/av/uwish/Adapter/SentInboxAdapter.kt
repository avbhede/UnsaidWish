package com.av.uwish.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.av.uwish.Model.InboxModel
import com.av.uwish.R


import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import de.hdodenhof.circleimageview.CircleImageView


import java.util.*

class SentInboxAdapter(internal var context: Context, resourse: Int, internal var hisModel: ArrayList<InboxModel>) : ArrayAdapter<InboxModel>(context, resourse, hisModel) {

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
        lateinit var profileimg: CircleImageView
        lateinit var wishtype:TextView
        lateinit var email:TextView
        lateinit var videolink:TextView
        lateinit var sent:TextView
        lateinit var waiting:TextView

        lateinit var exoPlayerView:SimpleExoPlayerView
        lateinit var sharedPreferences: SharedPreferences
        var exoPlayer: SimpleExoPlayer? = null

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder? = null
        val HistoryModel = getItem(position)
        if (convertView == null) {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.sent_inbox_layout, null, false)
            holder = ViewHolder()

            holder.mobile = convertView.findViewById(R.id.mobile)
            holder.wishtype = convertView.findViewById(R.id.wish_type)
            holder.datetime = convertView.findViewById(R.id.datetime)
            holder.name = convertView.findViewById(R.id.recname)
            holder.email = convertView.findViewById(R.id.email)
            holder.videolink = convertView.findViewById(R.id.videolink)
            holder.sent = convertView.findViewById(R.id.status_yes)
            holder.waiting = convertView.findViewById(R.id.status_no)




            holder.mobile.text = HistoryModel!!.mobile.toString()
            holder.wishtype.text = HistoryModel!!.wishtype.toString()
            holder.datetime!!.text = HistoryModel.postdate
            holder.name.text = HistoryModel!!.name.toString()
            holder.email.text = HistoryModel!!.email
            holder.videolink.text = HistoryModel!!.video

            if(HistoryModel!!.status == "true")
            {
                holder.sent.visibility = View.VISIBLE
                holder.waiting.visibility = View.GONE
            }

           /* try {
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
                holder.exoPlayer!!.setPlayWhenReady(true)
            } catch (e: Exception) {
                Log.e("MainAcvtivity", " exoplayer error $e")
            }

            */



        } else {
        }

        return convertView!!

    }

}
