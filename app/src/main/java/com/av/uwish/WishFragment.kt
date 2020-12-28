package com.av.uwish

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class WishFragment : Fragment() {

    lateinit var help_card:CardView
    lateinit var lastwish_card:CardView
    lateinit var conf_card:CardView
    lateinit var foru_card:CardView
    lateinit var hmoment_card:CardView

    lateinit var inbox:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview =  inflater.inflate(R.layout.fragment_wish, container, false)


        help_card= rootview.findViewById(R.id.help_wish)
        lastwish_card = rootview.findViewById(R.id.last_wish)
        conf_card = rootview.findViewById(R.id.confession_wish)
        foru_card = rootview.findViewById(R.id.its_me)
        hmoment_card = rootview.findViewById(R.id.happy_moment)
        inbox = rootview.findViewById(R.id.tvinbox)



        /*
        val myFab: FloatingActionButton = rootview.findViewById(R.id.fabinbox) as FloatingActionButton
        myFab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val activity = context as AppCompatActivity
                val myFragment: Fragment = InboxFragment()
                activity.supportFragmentManager.beginTransaction()
                    .add(R.id.maincontent, myFragment)
                    .replace(R.id.maincontent, myFragment).addToBackStack(null)
                    .commit()

            }
        })

         */
        inbox.setOnClickListener {

            val activity = context as AppCompatActivity
            val myFragment: Fragment = InboxFragment()
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.maincontent, myFragment)
                .replace(R.id.maincontent, myFragment).addToBackStack(null)
                .commit()

        }

        help_card.setOnClickListener {

            var intent = Intent(activity,WishUpload::class.java)
            intent.putExtra("wish_type","Safety First")
            startActivity(intent)

        }


        lastwish_card.setOnClickListener {

            var intent = Intent(activity,WishUpload::class.java)
            intent.putExtra("wish_type","Last Wish")
            startActivity(intent)

        }


        conf_card.setOnClickListener {

            var intent = Intent(activity,WishUpload::class.java)
            intent.putExtra("wish_type","Confession")
            startActivity(intent)

        }


        foru_card.setOnClickListener {

            var intent = Intent(activity,WishUpload::class.java)
            intent.putExtra("wish_type","Its me for u")
            startActivity(intent)

        }

        hmoment_card.setOnClickListener {

            var intent = Intent(activity,WishUpload::class.java)
            intent.putExtra("wish_type","Happy Moment")
            startActivity(intent)

        }

        return rootview
    }

}
