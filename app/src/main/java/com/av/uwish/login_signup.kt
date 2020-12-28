package com.av.uwish

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity


class login_signup : AppCompatActivity() {

    lateinit var viewPager:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)


        val fm = supportFragmentManager
        val fragment = fragment_login()
        fm.beginTransaction().add(R.id.mainlayout, fragment).commit()
        fm.beginTransaction().replace(R.id.mainlayout, fragment).commit()
        fm.beginTransaction().addToBackStack(null)


    }

}
