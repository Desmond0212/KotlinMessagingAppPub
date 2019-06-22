package com.example.kotlin_messaging_app

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashScreenActivity : AppCompatActivity() {

    private var lbl_PowerBy: TextView? = null
    private var lbl_Desmond: TextView? = null
    private var img_Logo: ImageView? = null
    private var lbl_Inla: TextView? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lbl_PowerBy = findViewById(R.id.lblPowerBy) as TextView
        lbl_Desmond = findViewById(R.id.lblDesmond) as TextView
        img_Logo = findViewById(R.id.imgLogo) as ImageView
        lbl_Inla = findViewById(R.id.lblINLA) as TextView

        val myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition)

        lbl_PowerBy!!.startAnimation(myanim)
        lbl_Desmond!!.startAnimation(myanim)
        img_Logo!!.startAnimation(myanim)
        lbl_Inla!!.startAnimation(myanim)

        val i = Intent(this, LoginActivity::class.java)
        val timer = object : Thread()
        {
            override fun run()
            {
                try
                {
                    Thread.sleep(5000)
                } catch (e: InterruptedException)
                {
                    e.printStackTrace()
                } finally
                {
                    startActivity(i)
                    finish()
                }
            }
        }
        timer.start()

        //Set android status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.statusBarColor = ContextCompat.getColor(this, R.color.layout_bg_color)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        }
    }
}
