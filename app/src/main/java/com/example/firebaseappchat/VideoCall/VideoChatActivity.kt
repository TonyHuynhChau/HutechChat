package com.example.firebaseappchat.VideoCall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.R
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import io.agora.rtc.Constants
import java.util.*

class VideoChatActivity : AppCompatActivity() {
    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "2046290dea6e45ce8dcb23e483ff359a"
    // Fill the channel name.
    private val CHANNEL = "call"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "0062046290dea6e45ce8dcb23e483ff359aIADpTWTM44vF2p1PyMpS+dRbpyJAuhCKnt8yUFYIqaviKD4vjswAAAAAEACHtvL/BId3YQEAAQACh3dh"

    private var mRtcEngine: RtcEngine ?= null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            //super.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
            setupRemoteVideo(uid)
        }
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            runOnUiThread {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid)
            }
        }
    }
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initializeAndJoinChannel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {

        }

        // Join the channel with a token.
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        //mRtcEngine!!.enableVideo()
        localVideoConfig()
        setupVideoProfile()
    }

    private fun localVideoConfig() {
        // Pass the SurfaceView object to Agora so that it renders the local video.
        val localContainer = findViewById(R.id.local_video_view_container) as FrameLayout
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(baseContext)
        localFrame.setZOrderMediaOverlay(true)
        localContainer.addView(localFrame)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun setupVideoProfile() {
        mRtcEngine?.enableAudio()
        mRtcEngine?.setVideoProfile(Constants.VIDEO_PROFILE_240P_3,false)
    }


    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = findViewById(R.id.remote_video_view_container) as FrameLayout

        val remoteFrame = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
        remoteFrame.setTag(uid)
    }
}