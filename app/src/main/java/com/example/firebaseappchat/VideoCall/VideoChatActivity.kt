package com.example.firebaseappchat.VideoCall

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.R
import android.view.View
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebaseappchat.databinding.ActivityNewMessBinding
import com.example.firebaseappchat.databinding.ActivityVideoChatBinding
import com.example.firebaseappchat.model.UserProfile
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import us.zoom.sdk.*


class VideoChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoChatBinding
    private lateinit var btnJoin: Button
    private lateinit var btnCreate: Button
    private lateinit var avatar: CircleImageView
    private lateinit var txtName: TextView
    private lateinit var txtNumRoom: EditText
    private lateinit var txtPassRoom: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentData = FirebaseAuth.getInstance().currentUser

        btnJoin = findViewById(R.id.btn_join)
        avatar = findViewById(R.id.iv_userCall_zoom)
        txtName = findViewById(R.id.txtName_Call)
        txtNumRoom = findViewById(R.id.txtMeetingNumber)
        txtPassRoom = findViewById(R.id.txtMeetingPassword)
        btnCreate = findViewById(R.id.btn_createRoom)

        if(currentData != null){
            readata(currentData.uid, avatar, txtName)
        }
        initializeZoom(this)
        initViews()

    }

    private fun initViews() {
        btnJoin.setOnClickListener {
            val roomOwner: String = txtName.text.toString()
            val meetingNumber: String = txtNumRoom.text.toString()
            val meetingPassword: String = txtPassRoom.text.toString()

            if (roomOwner.trim().isNotEmpty() && meetingNumber.trim().isNotEmpty() && meetingPassword.trim().isNotEmpty()){
                joinMeeting(this, meetingNumber, meetingPassword, roomOwner)
            } else {
                Toast.makeText(this, "Gọi Thất Bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnCreate.setOnClickListener {
            if (ZoomSDK.getInstance().isLoggedIn) {
                startMeeting(this)
            } else {
                createLoginDialog()
            }
        }
    }

    private fun readata(uid: String, avatar: ImageView, name2: TextView) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("$uid/name").value
                val photo = dataSnapshot.child("$uid/Urlphoto").value
                if (photo == null) {
                    Picasso.get().load(UserProfile.IMGURL).into(avatar)
                } else {
                    Picasso.get().load(photo.toString()).into(avatar)
                }
                name2.text = name.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("loadPost:onCancelled", error.toException())
            }
        }
        database.addValueEventListener(postListener)
    }


    private fun initializeZoom(context: Context) {
        val sdk = ZoomSDK.getInstance()

        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        val params = ZoomSDKInitParams().apply {
            appKey = "gkUUKbKwgb4fcf5rt59aRpxLvs2emUeGDkL6" // TODO: Retrieve your SDK key and enter it here
            appSecret = "uDkHzi9iUJ42wJStUZfphnPC0fQ2grXw5ylr" // TODO: Retrieve your SDK secret and enter it here
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }
        // TODO (optional): Add functionality to this listener (e.g. logs for debugging)
        val listener = object : ZoomSDKInitializeListener {
            /**
             * If the [errorCode] is [ZoomError.ZOOM_ERROR_SUCCESS], the SDK was initialized and can
             * now be used to join/start a meeting.
             */
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }

        sdk.initialize(context, listener, params)
    }

    private fun joinMeeting(context: Context, meetingNumber: String, pw: String, userName: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            displayName = userName // TODO: Enter your name
            meetingNo = meetingNumber
            password = pw
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    private fun login(username: String, password: String) {
        val result = ZoomSDK.getInstance().loginWithZoom(username, password)
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            //  listen for authentication result before starting a meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener)
        }
    }

    private val authListener = object : ZoomSDKAuthenticationListener {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        override fun onZoomSDKLoginResult(result: Long) {
            if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(this@VideoChatActivity)
            }
        }
        override fun onZoomIdentityExpired() = Unit
        override fun onZoomSDKLogoutResult(p0: Long) = Unit
        override fun onZoomAuthIdentityExpired() = Unit
    }

    private fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
    }

    private fun createLoginDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.activity_outcoming)
            .setPositiveButton("Log in") { dialog, _ ->
                dialog as AlertDialog
                val emailInput = dialog.findViewById<TextInputEditText>(R.id.email_input)
                val passwordInput = dialog.findViewById<TextInputEditText>(R.id.pw_input)
                val email = emailInput?.text?.toString()
                val password = passwordInput?.text?.toString()
                email?.takeIf { it.isNotEmpty() }?.let { emailAddress ->
                    password?.takeIf { it.isNotEmpty() }?.let { pw ->
                        login(emailAddress, pw)
                    }
                }
                dialog.dismiss()
            }.show()
    }

}



