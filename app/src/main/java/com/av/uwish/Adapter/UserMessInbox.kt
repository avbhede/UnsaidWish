package com.av.uwish.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.av.uwish.Model.InboxModel
import com.av.uwish.R
import com.av.uwish.UsersMessage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


import java.util.*

class UserMessInbox(internal var context: Context, resourse: Int, internal var hisModel: ArrayList<InboxModel>) : ArrayAdapter<InboxModel>(context, resourse, hisModel) {

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
        lateinit var name: TextView
        lateinit var img: CircleImageView
        lateinit var cardInbox:CardView

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder? = null
        val HistoryModel = getItem(position)
        if (convertView == null) {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.user_inbox_layout, null, false)
            holder = ViewHolder()

            holder.id = convertView.findViewById(R.id.mid)
            holder.img = convertView.findViewById(R.id.user_img)
            holder.name = convertView.findViewById(R.id.uname)
            holder.cardInbox = convertView.findViewById(R.id.cardinbox)


            holder.id.text = HistoryModel!!.id.toString()
            holder.name.text = HistoryModel!!.name.toString()


            if(HistoryModel.img_url !="")
            {
                Picasso
                    .get()
                    .load(HistoryModel.img_url)
                    .into(holder.img)
            }
            else
            {
                holder.img.setImageResource(R.drawable.ic_person2)
            }

            holder.cardInbox.setOnClickListener {

                var intent= Intent(context, UsersMessage::class.java)
                intent.putExtra("mid", HistoryModel.id)
                intent.putExtra("name", HistoryModel.name)
                context.startActivity(intent)

            }


        } else {
        }

        return convertView!!

    }

}
