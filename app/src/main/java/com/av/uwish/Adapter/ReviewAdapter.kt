package com.av.uwish.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.av.uwish.R
import com.av.uwish.Model.ReviewModel
import java.util.*

class ReviewAdapter(internal var context: Context, resourse: Int, internal var hisModel: ArrayList<ReviewModel>) : ArrayAdapter<ReviewModel>(context, resourse, hisModel) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getCount(): Int {
        return hisModel.size
    }


    override fun getItem(position: Int): ReviewModel? {
        return hisModel[position]
    }
    override fun getItemId(position: Int): Long {
        return 0
    }


    inner class ViewHolder
    {

        lateinit var id:TextView
        internal var rev_cname: TextView? = null
        internal var rev_mesg: TextView? = null
        internal var rev_date: TextView? = null
        internal var rev_rating:RatingBar?=null


    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        var holder: ViewHolder? = null
        val HistoryModel = getItem(position)
        if (convertView == null) {

            val layoutInflator = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflator.inflate(R.layout.review_layout, null, false)
            holder = ViewHolder()

            holder.rev_cname= convertView!!.findViewById(R.id.tv_RevCName)
            holder.rev_mesg = convertView.findViewById(R.id.tv_RevMesg)
            holder.rev_date = convertView.findViewById(R.id.tv_RevDate)
            holder.rev_rating= convertView.findViewById(R.id.RevRatingBar)


            holder.rev_cname!!.text = HistoryModel!!.rev_name
            holder.rev_date!!.text = HistoryModel.rev_date
            holder.rev_mesg!!.text = HistoryModel.rev_mesg
            holder.rev_rating!!.rating= HistoryModel.rev_rating!!.toFloat()

        } else {



        }


        return convertView

    }

}