package com.example.firebaseappchat.messages

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "chat log"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: SignUpActivity.getUser? = null
    var AnDanh: SignUpActivity.getUser? = null
    var check: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        check = intent.getBooleanExtra("Check", false)
        Log.d("CHECKANDANH", check.toString())

        toUser = intent.getParcelableExtra(NewMessActivity.USER_KEY)
        AnDanh = intent.getParcelableExtra("AnDanh")
        Log.d("CHECKTHONGTIN", AnDanh?.email.toString())
        supportActionBar?.title = toUser?.name

        nhantinnhan()

        gui_button_chat_log.setOnClickListener {
            performsendMessage()
        }
        editText_chat_log.setOnClickListener {
            if (editText_chat_log.text.toString() != "") {
                performsendMessage()
            }
        }
    }

    override fun onBackPressed() {
        if (!check) {
            super.onBackPressed()
        } else {
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<SignUpActivity.getUser>("AnDanh")
            val toId = user?.uid

            //Xóa tin nhan tu nguoi gui
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                .removeValue()
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                .removeValue()
            //Xóa tin nhan tu nguoi nhan
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
                .removeValue()
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                .removeValue()
            super.onBackPressed()
        }
    }

    private fun nhantinnhan() {
        if (check == true) {
            val fromId = FirebaseAuth.getInstance().uid
            val toId = AnDanh?.uid
            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
            ref.addChildEventListener(object : ChildEventListener {
                @SuppressLint("SimpleDateFormat")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    chatMessage?.text?.let { Log.d(TAG, it) }
                    val sdf = SimpleDateFormat("hh:mm")
                    if (chatMessage != null) {
                        if (chatMessage.formId == FirebaseAuth.getInstance().uid) {
                            val currentUser = MainActivity.currentUser ?: return

                            adapter.add(
                                ChatFromItem(
                                    chatMessage.text,
                                    sdf.format(chatMessage.timestamp),
                                    currentUser,
                                    check
                                )
                            )
                        } else {
                            adapter.add(
                                ChatToItem(
                                    chatMessage.text,
                                    sdf.format(chatMessage.timestamp),
                                    AnDanh!!,
                                    check
                                )
                            )
                        }
                    }
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                @SuppressLint("LongLogTag")
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("THÔNG BÁO XÓA TIN NHẮN BÊN CHATLOG:","THÀNH CÔNG")
                }
            })
        } else {
            val fromId = FirebaseAuth.getInstance().uid
            val toId = toUser?.uid
            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
            ref.addChildEventListener(object : ChildEventListener {
                @SuppressLint("SimpleDateFormat")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    chatMessage?.text?.let { Log.d(TAG, it) }
                    val sdf = SimpleDateFormat("hh:mm")
                    if (chatMessage != null) {
                        if (chatMessage.formId == FirebaseAuth.getInstance().uid) {
                            val currentUser = MainActivity.currentUser ?: return

                            adapter.add(
                                ChatFromItem(
                                    chatMessage.text,
                                    sdf.format(chatMessage.timestamp),
                                    currentUser,
                                    check
                                )
                            )
                        } else {
                            adapter.add(
                                ChatToItem(
                                    chatMessage.text,
                                    sdf.format(chatMessage.timestamp),
                                    toUser!!,
                                    check
                                )
                            )
                        }
                    }
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }
            })
        }

    }


    private fun performsendMessage() {
        if (check == true) {
            // gui du lieu vao firebase
            editText_chat_log.text.toString()
            val text = editText_chat_log.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<SignUpActivity.getUser>("AnDanh")
            val toId = user?.uid

            if (fromId == null) return

            // tin nhan tu nguoi gui
            val reference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
            //tin nhan tu nguoi nhan
            val toReference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
            val chatMessage = toId?.let {
                ChatMessage(
                    reference.key!!, text, fromId!!,
                    it, System.currentTimeMillis(), check
                )
            }
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "luu thong tin: ${reference.key}")
                    editText_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
            toReference.setValue(chatMessage)

            val latestMessagesRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessagesRef.setValue(chatMessage)
            val latestMessagesToRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessagesToRef.setValue(chatMessage)
        } else {
            // gui du lieu vao firebase
            editText_chat_log.text.toString()
            val text = editText_chat_log.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)
            val toId = user?.uid

            if (fromId == null) return

            // tin nhan tu nguoi gui
            val reference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
            //tin nhan tu nguoi nhan
            val toReference =
                FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
            val chatMessage = toId?.let {
                ChatMessage(
                    reference.key!!, text, fromId!!,
                    it, System.currentTimeMillis(), check
                )
            }
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "luu thong tin: ${reference.key}")
                    editText_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
            toReference.setValue(chatMessage)

            val latestMessagesRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessagesRef.setValue(chatMessage)
            val latestMessagesToRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessagesToRef.setValue(chatMessage)
        }
    }
}


class ChatFromItem(
    val text: String,
    val time: String,
    val user: SignUpActivity.getUser,
    val check: Boolean
) :
    Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (check == true) {
            viewHolder.itemView.textviewfrom_chat_from_row.text = text
            viewHolder.itemView.Txt_time_From.text = time

            val url = user.Urlphoto
            val targetImageView = viewHolder.itemView.imageView_chat_from_row
            if (url.isEmpty()) {
                Picasso.get().load(IMGURL).into(targetImageView)
            } else {
                Picasso.get().load(url).into(targetImageView)
            }
        } else {
            viewHolder.itemView.textviewfrom_chat_from_row.text = text
            viewHolder.itemView.Txt_time_From.text = time

            val url = user.Urlphoto
            val targetImageView = viewHolder.itemView.imageView_chat_from_row
            if (url.isEmpty()) {
                Picasso.get().load(IMGURL).into(targetImageView)
            } else {
                Picasso.get().load(url).into(targetImageView)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(
    val text: String,
    val time: String,
    val user: SignUpActivity.getUser,
    val check: Boolean
) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (check == true) {
            viewHolder.itemView.textviewfrom_chat_to_row.text = text
            viewHolder.itemView.Txt_time_To.text = time


            val targetImageView = viewHolder.itemView.imageViewchat_to_row
            Picasso.get().load(R.drawable.andanh).into(targetImageView)
        } else {
            viewHolder.itemView.textviewfrom_chat_to_row.text = text
            viewHolder.itemView.Txt_time_To.text = time

            val url = user.Urlphoto
            val targetImageView = viewHolder.itemView.imageViewchat_to_row
            if (url.isEmpty()) {
                Picasso.get().load(IMGURL).into(targetImageView)
            } else {
                Picasso.get().load(url).into(targetImageView)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}