package com.example.firebaseappchat.VideoCall


import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.core.view.isVisible
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import fragment.HomeFragment
import kotlinx.android.synthetic.main.user_row.view.*
import kotlinx.android.synthetic.main.user_row_call.view.*
import java.util.*
import kotlin.collections.HashMap

class Outcoming : AppCompatActivity() {
    private lateinit var username: TextView
    private lateinit var btnCancell: ImageView
    private lateinit var btnAccept: ImageView
    private lateinit var avatar: CircleImageView
    private lateinit var mediaPlayer: MediaPlayer

    var toUser: SignUpActivity.getUser? = null
    private lateinit var ocuRef: DatabaseReference

    private lateinit var reciverUid: String

    private lateinit var senderUid: String

    private lateinit var callingUid: String

    private lateinit var ringingUid: String

    private lateinit var checker: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outcoming)
        val user: SignUpActivity.getUser
        toUser = intent.getParcelableExtra(VideoChatActivity.USER_KEY)

        reciverUid = intent.extras?.get(VideoChatActivity.USER_KEY).toString()
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        username= findViewById(R.id.txtUsername_incomingCall)
        avatar = findViewById(R.id.img_userIncomingCall)
        btnCancell = findViewById(R.id.btn_rejectcall)
        btnAccept = findViewById(R.id.btn_accpetcall)


        mediaPlayer = MediaPlayer.create(this, R.raw.ringing)

      //  mediaPlayer = MediaPlayer.create(this, R.raw.Ringing)

        checker = "clicked"

        if (toUser != null) {
            username.text = toUser!!.name
            Picasso.get().load(toUser!!.Urlphoto).into(avatar)
        }

        btnCancell.setOnClickListener(){
            checker = "clicked"
            mediaPlayer.stop()
            cancelCalling()
        }

        btnAccept.setOnClickListener {
            mediaPlayer.stop()
            var callPickUp: HashMap<String, Any> = HashMap()

            callPickUp.put("picked","picked")

            ocuRef.child(senderUid).child("Ringing").updateChildren(callPickUp)
                .addOnCompleteListener {
                    startActivity(Intent(this@Outcoming,IncomingCall::class.java))
                }
        }
    }

    private fun cancelCalling() {
        //Sender
        ocuRef.child(senderUid).child("calling").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.hasChild("calling")) {
                    callingUid = snapshot.child("calling").value.toString()

                    ocuRef.child(callingUid).child("ringing").removeValue().addOnCompleteListener {
                        if (it.isSuccessful){
                            ocuRef.child(senderUid).child("calling").removeValue().addOnCompleteListener {
                                startActivity(Intent(this@Outcoming, IncomingCall::class.java))
                                finish()
                            }
                        }
                    }
                }
                else{
                    startActivity(Intent(this@Outcoming, HomeFragment::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //Reciveder
        ocuRef.child(senderUid).child("Ringing").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.hasChild("ringing")) {
                    callingUid = snapshot.child("ringing").value.toString()

                    ocuRef.child(ringingUid).child("calling").removeValue().addOnCompleteListener {
                        if (it.isSuccessful){
                            ocuRef.child(senderUid).child("calling").removeValue().addOnCompleteListener {
                                startActivity(Intent(this@Outcoming, IncomingCall::class.java))
                                finish()
                            }
                        }
                    }
                }
                else{
                    startActivity(Intent(this@Outcoming, MainActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onStart() {
        super.onStart()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        mediaPlayer.start()
        //Call Event
        ocuRef = FirebaseDatabase.getInstance().getReference("/user-call/$fromId/$toId")
        ocuRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!checker.equals("clicked") && !snapshot.hasChild("calling") && !snapshot.hasChild("ringing")){

                    var callingInfo: HashMap<String, Any> = HashMap()

                    callingInfo.put("calling", reciverUid)

                        ocuRef.child(senderUid).child("calling")
                        .updateChildren(callingInfo as Map<String, Any>)
                        .addOnCompleteListener {
                            fun onComplete(@NonNull task: Task<Void>) {
                                if(task.isSuccessful()){
                                    var ringingInfo: HashMap<String, Any> = HashMap()

                                    ringingInfo.put("ringing", senderUid)

                                    ocuRef.child(reciverUid).child("ringing")
                                        .updateChildren(ringingInfo as Map<String, Any>)
                                }
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //Cancal button
        ocuRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(senderUid).hasChild("ringing") && snapshot.child(senderUid).hasChild("calling")){
                    btnAccept.visibility = View.VISIBLE
                }
                if(snapshot.child(reciverUid).child("Ringing").hasChild("picked")){
                    mediaPlayer.stop()
                    startActivity(Intent(this@Outcoming,IncomingCall::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}

