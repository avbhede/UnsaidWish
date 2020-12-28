package com.aig.extracoaching.Adapter


import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.av.uwish.Model.HomeVideoModel
import com.av.uwish.R
import com.av.uwish.Retrofit.RetrofitClient
import com.av.uwish.User_fragment
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class HomeVideoAdapter(internal var context: Context, resourse: Int, internal var hisModel: ArrayList<HomeVideoModel>) : ArrayAdapter<HomeVideoModel>(context, resourse, hisModel) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getCount(): Int {
        return hisModel.size
    }


    override fun getItem(position: Int): HomeVideoModel? {
        return hisModel[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    inner class ViewHolder
    {

        lateinit var id:TextView
        lateinit var like_count: TextView
        lateinit var deslike_count: TextView
        lateinit var like:ImageView
        lateinit var deslike:ImageView
        lateinit var report:ImageView
        lateinit var exoPlayerView:SimpleExoPlayerView
        lateinit var post_uname:TextView
        lateinit var post_date:TextView
        lateinit var post_prof: CircleImageView
        lateinit var sharedPreferences: SharedPreferences
        lateinit var userid:String
        lateinit var follow:TextView
        var exoPlayer: SimpleExoPlayer? = null

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder? = null
        val HistoryModel = getItem(position)

        if (convertView == null) {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.home_video_layout, null, false)
            holder = ViewHolder()

            holder.id = convertView!!.findViewById(R.id.vdo_id)
            holder.like_count = convertView.findViewById(R.id.like_count)
            holder.deslike_count = convertView.findViewById(R.id.deslike_count)
            holder.like = convertView.findViewById(R.id.like)
            holder.deslike = convertView.findViewById(R.id.deslike)
            holder.report = convertView.findViewById(R.id.report)
            holder.exoPlayerView = convertView.findViewById(R.id.v1)
            holder.follow = convertView.findViewById(R.id.follow)

            holder.post_prof = convertView.findViewById(R.id.post_profile)

            holder.post_date = convertView.findViewById(R.id.post_date)
            holder.post_uname = convertView.findViewById(R.id.post_uname)

            if(HistoryModel!!.post_profile.toString() != "")
            {
                Picasso.get().load(HistoryModel!!.post_profile.toString()).into(holder.post_prof)

            }
            else
            {
                holder.post_prof.setImageResource(R.drawable.ic_person2)
            }


            holder.sharedPreferences =context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            holder.userid = holder.sharedPreferences.getString("id","").toString()

            holder.id.text = HistoryModel!!.v_id.toString()
            holder.like_count.text = HistoryModel!!.like_count.toString()
            holder.deslike_count!!.text = HistoryModel.deslike_count
            holder.post_date.text = HistoryModel!!.post_date.toString()
            holder.post_uname!!.text = HistoryModel.post_name

            try {
                val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
                val trackSelector: TrackSelector = DefaultTrackSelector(
                        AdaptiveTrackSelection.Factory(bandwidthMeter)
                )
                holder.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

                val dataSourceFactory =DefaultHttpDataSourceFactory("exoplayer_video")
                val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
                val mediaSource: MediaSource = ExtractorMediaSource(
                        Uri.parse(HistoryModel.vdo_url),
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


            if(HistoryModel!!.follow_status.toString() == "true")
            {
                holder.follow.text = "Following"
            }

            if(HistoryModel!!.like_status.toString() == "true")
            {
                holder.like.setImageResource(R.drawable.ic_like_active)
                holder.deslike.setImageResource(R.drawable.ic_thumb_down)
            }
            else if(HistoryModel!!.like_status.toString() == "false")
            {
                holder.like.setImageResource(R.drawable.ic_thumb_up)
                holder.deslike.setImageResource(R.drawable.ic_broken_heart)
            }else
            {
                holder.like.setImageResource(R.drawable.ic_thumb_up)
                holder.deslike.setImageResource(R.drawable.ic_thumb_down)
            }

            if(HistoryModel!!.report.toString() == "report")
            {
                holder.report.setImageResource(R.drawable.ic_report)
            }

            // Visite Profile
            holder.post_uname.setOnClickListener(View.OnClickListener { v ->

                holder.sharedPreferences = context!!.getSharedPreferences("VISIT", Context.MODE_PRIVATE)
                val editor = holder.sharedPreferences.edit()
                editor.putString("uid", HistoryModel.uid)
                editor.putString("uname", HistoryModel.post_name)
                editor.putString("follow_status", HistoryModel!!.follow_status.toString())
                editor.commit()

                val activity = v.context as AppCompatActivity
                val myFragment: Fragment = User_fragment()
                activity.supportFragmentManager.beginTransaction()
                        .add(R.id.maincontent, myFragment)
                        .replace(R.id.maincontent, myFragment).addToBackStack(null)
                        .commit()
            })


            // Report Submite

            holder.report.setOnClickListener {

                holder.report.setImageResource(R.drawable.ic_report)

                val call = RetrofitClient.instance.api.like(holder.userid,holder.id.text.toString(),"report")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {

                            val s = response.body()!!.string()

                            val jsonObject = JSONObject(s)

                            Log.v("STATUS",jsonObject.getBoolean("status").toString())

                            if (jsonObject.getBoolean("status")) {

                                Log.v("Response_like",jsonObject.getString("message"))

//                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                            }
                            else {
                                Log.v("Response_like",jsonObject.getString("message"))

//                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

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


            // Like Submite

            holder.like.setOnClickListener {


                if(HistoryModel!!.like_status.toString() == "true")
                {
                    holder.like.setImageResource(R.drawable.ic_thumb_up)
                    var l_count:Int = HistoryModel!!.like_count!!.toInt()

                    if(l_count!=0)
                    {
                        holder.like_count.text = (l_count - 1).toString()
                    }
                    HistoryModel!!.like_status = "false"

                }
                else
                {
                    holder.like.setImageResource(R.drawable.ic_like_active)
                    holder.deslike.setImageResource(R.drawable.ic_thumb_down)

                    var l_count:Int = HistoryModel!!.like_count!!.toInt()
                    var d_count:Int = HistoryModel!!.deslike_count!!.toInt()

                    if(d_count!=0)
                    {
                        holder.deslike_count.text = (l_count - 1).toString()

                    }

                    holder.like_count.text = (l_count + 1).toString()
                    HistoryModel!!.like_status = "true"

                }


                val call = RetrofitClient.instance.api.like(holder.userid,holder.id.text.toString(),"true")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {

                            val s = response.body()!!.string()

                            val jsonObject = JSONObject(s)

                            Log.v("STATUS",jsonObject.getBoolean("status").toString())

                            if (jsonObject.getBoolean("status")) {

                                // Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                                Log.v("Response_like",jsonObject.getString("message"))
                            }
                            else {
                                Log.v("Response_like",jsonObject.getString("message"))
                                // Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

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


            // DesLike Submite

            holder.deslike.setOnClickListener {



                if(HistoryModel!!.like_status.toString() == "false")
                {

                    holder.deslike.setImageResource(R.drawable.ic_thumb_down)

                    var d_count:Int = HistoryModel!!.deslike_count!!.toInt()

                    if(d_count!=0)
                    {
                        holder.deslike_count.text = (d_count-1).toString()
                    }
                    HistoryModel!!.like_status = "true"

                }
                else
                {

                    holder.deslike.setImageResource(R.drawable.ic_broken_heart)
                    holder.like.setImageResource(R.drawable.ic_thumb_up)

                    var d_count:Int = HistoryModel!!.deslike_count!!.toInt()
                    var l_count:Int = HistoryModel!!.like_count!!.toInt()


                    holder.deslike_count.text = (d_count + 1).toString()

                    if(l_count!=0)
                    {
                        holder.like_count.text = (l_count - 1).toString()

                    }

                    HistoryModel!!.like_status = "false"

                }


                val call = RetrofitClient.instance.api.like(holder.userid,holder.id.text.toString(),"false")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {

                            val s = response.body()!!.string()

                            val jsonObject = JSONObject(s)

                            Log.v("STATUS",jsonObject.getBoolean("status").toString())

                            if (jsonObject.getBoolean("status")) {

                                Log.v("Response_like",jsonObject.getString("message"))

//                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                            }
                            else {
                                Log.v("Response_like",jsonObject.getString("message"))

                                //                       Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show()

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


            holder.follow.setOnClickListener {

                val call = RetrofitClient.instance.api.follow(holder.userid,HistoryModel!!.uid.toString())
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {

                            val s = response.body()!!.string()

                            val jsonObject = JSONObject(s)

                            Log.v("STATUS",jsonObject.getBoolean("status").toString())

                            if (jsonObject.getBoolean("status")) {

                                Log.v("Response_Follow",jsonObject.getString("message"))
                                holder.follow.text="Following"

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


        } else {

        }


        return convertView

    }

}
