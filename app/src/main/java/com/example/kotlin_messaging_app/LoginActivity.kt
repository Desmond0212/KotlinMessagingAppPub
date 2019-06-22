package com.example.kotlin_messaging_app

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()
{

    private var txt_email: EditText? = null
    private var txt_password: EditText? = null
    private var btn_login: Button? = null
    private var lbl_email: TextInputLayout? = null
    private var lbl_password: TextInputLayout? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txt_email = findViewById<EditText>(R.id.txtLogin_Username)
        txt_password = findViewById<EditText>(R.id.txtLogin_Password)
        btn_login = findViewById<Button>(R.id.btnLogin)
        lbl_email = findViewById<TextInputLayout>(R.id.input_layout_email)
        lbl_password = findViewById<TextInputLayout>(R.id.input_layout_password)

        btnLogin_SignUp.setOnClickListener {

            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
