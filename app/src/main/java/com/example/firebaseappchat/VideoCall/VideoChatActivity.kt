package com.example.firebaseappchat.VideoCall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.R
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout;
import java.util.*
import android.content.Intent
import android.net.Uri


class VideoChatActivity : AppCompatActivity() {
    private val MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555
    private val LOG_TAG = "AndroidExample"
    private lateinit var editTextPhoneNum: EditText
    private lateinit var buttoncall: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.firebaseappchat.R.layout.activity_video_chat)
        this.editTextPhoneNum = findViewById<View>(com.example.firebaseappchat.R.id.editText1) as EditText
        this.buttoncall = findViewById<View>(com.example.firebaseappchat.R.id.button1) as Button

        buttoncall.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // TODO Auto-generated method stub
                val i = Intent(Intent.ACTION_CALL)
                i.data = Uri.parse("tel:" + editTextPhoneNum.getText().toString())
                startActivity(i)
            }
        })
    }


}