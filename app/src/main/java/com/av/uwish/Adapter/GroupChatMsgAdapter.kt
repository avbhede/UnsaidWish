package com.av.uwish.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.av.uwish.Model.ChatModel
import com.av.uwish.R
import java.util.ArrayList

class GroupChatMsgAdapter( context: Context, resourse: Int,  var groupChatModel: ArrayList<ChatModel>) : ArrayAdapter<ChatModel>(context, resourse, groupChatModel) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getCount(): Int {
        return groupChatModel.size
    }


    override fun getItem(position: Int): ChatModel? {
        return groupChatModel[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    inner class ViewHolder {

        lateinit var tv_msg_sent: TextView
        lateinit var tv_mes_rec:TextView

        lateinit var tv_msg_sent_time: TextView
        lateinit var tv_msg_rec_time: TextView

        lateinit var msg_sent_layout:ConstraintLayout
        lateinit var msg_rec_layout: ConstraintLayout
        lateinit var sharedPreferences: SharedPreferences

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder

        val chatModel = getItem(position)

        if (convertView == null)
        {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.chat_msg_layout, null, false)

            holder = ViewHolder()

            holder.sharedPreferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

            var mobile:String=holder.sharedPreferences.getString("id","").toString()

            holder.msg_sent_layout = convertView.findViewById(R.id.sent_msg_layout)
            holder.msg_rec_layout = convertView.findViewById(R.id.rec_msg_layout)

            holder.tv_mes_rec= convertView.findViewById(R.id.text_rec_msg)
            holder.tv_msg_rec_time = convertView.findViewById(R.id.text_rec_msg_time)

            holder.tv_msg_sent = convertView.findViewById(R.id.text_send_msg_body)
            holder.tv_msg_sent_time = convertView.findViewById(R.id.text_send_msg_time)


            var checkMob:String=chatModel!!.mem_mob.toString()
            var checkMsg:String=chatModel.message.toString()

            Log.v("CHeck Data",checkMsg+" "+checkMob)

            if(checkMob==mobile && checkMsg!="")
            {

                holder.msg_sent_layout!!.visibility=View.VISIBLE

                holder.tv_msg_sent.text=chatModel.message
                holder.tv_msg_sent_time.text=chatModel.msg_time

            }
            else if(checkMob!=mobile && checkMsg!="")
            {

                holder.msg_rec_layout!!.visibility=View.VISIBLE

                holder.tv_mes_rec.text=chatModel.message
                holder.tv_msg_rec_time.text=chatModel.msg_time

                convertView.tag = holder
            }


        } else { }

        return convertView!!

    }

}


