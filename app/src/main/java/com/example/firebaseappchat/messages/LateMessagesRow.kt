package com.example.firebaseappchat.messages

import android.annotation.SuppressLint
import android.util.Log
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.text.SimpleDateFormat


class LateMessagesRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartnerUser: SignUpActivity.getUser? = null

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (!chatMessage.check) {
            val sdf = SimpleDateFormat("hh:mm")
            val chatPartnerId: String
            if (chatMessage.text.length > 7) {
                chatMessage.text = "Đã gửi 1 tin nhắn"
            }
            if (chatMessage.formId == FirebaseAuth.getInstance().uid) {
                viewHolder.itemView.message_textview_latest_messages.text =
                    "Bạn: ${chatMessage.text}. -${sdf.format(chatMessage.timestamp)}"
                chatPartnerId = chatMessage.toId
            } else {
                viewHolder.itemView.message_textview_latest_messages.text =
                    "-: ${chatMessage.text}. -${sdf.format(chatMessage.timestamp)}"
                chatPartnerId = chatMessage.formId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/user/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue((SignUpActivity.getUser::class.java))

                    val targetImageView = viewHolder.itemView.imageview_lastest_messages
                    viewHolder.itemView.username_textView_latestmessage.text = chatPartnerUser?.name
                    if (chatPartnerUser?.Urlphoto?.isEmpty() == true)
                        Picasso.get().load(IMGURL).into(targetImageView)
                    else
                        Picasso.get().load(chatPartnerUser?.Urlphoto).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            val sdf = SimpleDateFormat("hh:mm")
            val chatPartnerId: String
            if (chatMessage.text.length > 7) {
                chatMessage.text = "Đã gửi 1 tin nhắn"
            }
            if (chatMessage.formId == FirebaseAuth.getInstance().uid) {
                viewHolder.itemView.message_textview_latest_messages.text =
                    "Bạn: ${chatMessage.text}. -${sdf.format(chatMessage.timestamp)}"
                chatPartnerId = chatMessage.toId
            } else {
                viewHolder.itemView.message_textview_latest_messages.text =
                    "-: ${chatMessage.text}. -${sdf.format(chatMessage.timestamp)}"
                chatPartnerId = chatMessage.formId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/user/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue((SignUpActivity.getUser::class.java))
                    //  viewHolder.itemView.username_textView_latestmessage.text = chatPartnerUser?.name
                    //   if (chatPartnerUser?.Urlphoto?.isEmpty() == true)
                    Picasso.get().load(R.drawable.andanh).into(viewHolder.itemView.imageview_lastest_messages)
                    //  else
                    //         Picasso.get().load(chatPartnerUser?.Urlphoto).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}
