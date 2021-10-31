package com.example.firebaseappchat.VideoCall

import android.Manifest.permission.INTERNET
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.opentok.android.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest

const val RC_VIDEO_APP_PERM: Int = 124
class IncomingCall : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener{
    private lateinit var btnClose: ImageView
    private lateinit var userRef: DatabaseReference
    private lateinit var pubController: FrameLayout
    private lateinit var subController: FrameLayout

    var toUser: SignUpActivity.getUser? = null
    private lateinit var userId: String
    lateinit var mSession: Session
    lateinit var mPublisher: Publisher
    lateinit var mSubscriber: Subscriber

    private var API_KEY: String = "47367941"
    private var SESSION_ID: String = "1_MX40NzM2Nzk0MX5-MTYzNTU4NzM4MTQyN34vYytyMUJjekhyZGQ3RDl0WWdXWVFoSFR-fg"
    private var TOKEN: String = "T1==cGFydG5lcl9pZD00NzM2Nzk0MSZzaWc9NmM5ODlhYTI1NmFlOTc3ZjI3ZWUzMWMxYmZiNWI1MzMyYWI2MDEwOTpzZXNzaW9uX2lkPTFfTVg0ME56TTJOemswTVg1LU1UWXpOVFU0TnpNNE1UUXlOMzR2WXl0eU1VSmpla2h5WkdRM1JEbDBXV2RYV1ZGb1NGUi1mZyZjcmVhdGVfdGltZT0xNjM1NTg3NDI1Jm5vbmNlPTAuNDY5MDAwMzc1MTQ3ODQxNzYmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYzODE3OTQyNCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ=="
    private lateinit var LOG_TAG: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        userRef = FirebaseDatabase.getInstance().reference.child("/user-call/$fromId/$toId")
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        btnClose = findViewById(R.id.close_video_btn)
        btnClose.setOnClickListener {
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(userId).hasChild("Ringing")){
                        userRef.child(userId).child("Ringing").removeValue()

                        if(mPublisher != null){
                            mPublisher.destroy()
                        }
                        if(mSubscriber != null){
                            mSubscriber.destroy()
                        }

                        startActivity(Intent(this@IncomingCall, MainActivity::class.java))
                        finish()
                    }

                    if (snapshot.child(userId).hasChild("Calling")){
                        userRef.child(userId).child("Calling").removeValue()
                        startActivity(Intent(this@IncomingCall, MainActivity::class.java))
                        finish()
                    }

                    else{
                        startActivity(Intent(this@IncomingCall, LoginActivity::class.java))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        requestPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this@IncomingCall)
    }


    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermission(){

        val perf: String = (INTERNET)
        val perf1: String = (CAMERA)
        val perf2: String = (RECORD_AUDIO)

        if(EasyPermissions.hasPermissions(this,perf,perf1,perf2)){
            pubController = findViewById(R.id.pub_container)
            subController = findViewById(R.id.sub_container)

            mSession = Session.Builder(this,API_KEY,SESSION_ID).build()

            mSession.setSessionListener(this@IncomingCall)
            mSession.connect(TOKEN)
        }
        else{
            EasyPermissions.requestPermissions(this,"Head Camera and Mic Permisson...",RC_VIDEO_APP_PERM)
        }
    }

    override fun onConnected(p0: Session?) {
        Log.i(LOG_TAG, "Session Connect")
        mPublisher = Publisher.Builder(this).build()
        mPublisher.setPublisherListener(this)

        pubController.addView(mPublisher.view)


        if (mPublisher.view is GLSurfaceView) {
            (mPublisher.view as GLSurfaceView).setZOrderOnTop(true) as GLSurfaceView
        }

        mSession.publish(mPublisher)
    }

    override fun onDisconnected(p0: Session?) {
        TODO("Not yet implemented")
    }

    override fun onStreamReceived(p0: Session?, p1: Stream?) {
        Log.i(LOG_TAG,"Stream Receiced")
        if(mSubscriber == null){
            mSubscriber = Subscriber.Builder(this@IncomingCall,p1).build()
            mSession.subscribe(mSubscriber)
            subController.addView(mSubscriber.view)
        }
    }

    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        Log.i(LOG_TAG,"Stream Dropped")
        if (mSubscriber != null)
        {
            mSubscriber == null
            subController.removeAllViews()
        }
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        Log.i(LOG_TAG,"Stream Receiced")
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
        TODO("Not yet implemented")
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        TODO("Not yet implemented")
    }

}

