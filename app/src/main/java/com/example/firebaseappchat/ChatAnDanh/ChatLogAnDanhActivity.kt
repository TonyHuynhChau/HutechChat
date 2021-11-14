package com.example.firebaseappchat.ChatAnDanh

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.giphy.sdk.analytics.GiphyPingbacks.context
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatLogAnDanhActivity : AppCompatActivity(), GiphyDialogFragment.GifSelectionListener {

    companion object {
        val TAG = "chat log"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    var selectPhotoUrl: Uri? = null
    lateinit var GuiAnh: ImageView
    lateinit var GIF: ImageView
    lateinit var emoji: ImageView
    lateinit var recyclerview_chat_log: RecyclerView
    var toUser: SignUpActivity.getUser? = null
    private var Loading: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        Load()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        //Emoji
        EmojiManager.install(GoogleEmojiProvider())
        val popup =
            EmojiPopup.Builder.fromRootView(findViewById(R.id.rootView)).build(editText_chat_log)
        emoji.setOnClickListener {
            popup.toggle()
        }

        GIF.setOnClickListener {
            GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
        }

        GuiAnh.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
            Loading = ProgressDialog(this)
            Loading!!.setTitle("Thông Báo")
            Loading!!.setMessage("Xin Đợi Trong Giây Lát")
            Loading!!.show()
        }
        nhantinnhan()

        gui_button_chat_log.setOnClickListener {
            performsendMessage("")
        }
    }

    private fun Load() {
        Giphy.configure(this, "pEUOapu4NFuNtErm4LOfR4VlhXsTioxy")
        GIF = findViewById(R.id.GIF)
        GuiAnh = findViewById(R.id.BtnGuiAnh)
        emoji = findViewById(com.example.firebaseappchat.R.id.emoji)
        recyclerview_chat_log = findViewById(R.id.recyclerview_chat_log)
        recyclerview_chat_log.adapter = adapter
        toUser = intent.getParcelableExtra(NewMessActivity.USER_KEY)
        supportActionBar?.title = "Ẩn Danh"
    }


    private fun updateImages() {
        if (selectPhotoUrl != null) {
            //code thành công tới lưu vào storage images
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("ImagesMessage/$filename")
            ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
                //Lấy URL Của Ảnh
                ref.downloadUrl.addOnSuccessListener {
                    performsendMessage(it.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUrl = data.data
            updateImages()
        }
    }

    private fun nhantinnhan() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages-andanh/$fromId/$toId")
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
                                chatMessage.anh
                            )
                        )
                    } else {
                        adapter.add(
                            ChatToItem(
                                chatMessage.text,
                                sdf.format(chatMessage.timestamp),
                                toUser!!,

                                chatMessage.anh
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
            }
        })
    }

    private fun performsendMessage(photoUrl: String) {

        // gui du lieu vao firebase
        editText_chat_log.text.toString()
        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        // tin nhan tu nguoi gui
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages-andanh/$fromId/$toId")
                .push()
        //tin nhan tu nguoi nhan
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages-andanh/$toId/$fromId")
                .push()
        val chatMessage = toId?.let {
            ChatMessage(
                reference.key!!, text, fromId!!,
                it, System.currentTimeMillis(), photoUrl
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
            FirebaseDatabase.getInstance().getReference("/latest-messages-andanh/$fromId/$toId")
        latestMessagesRef.setValue(chatMessage)
        val latestMessagesToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages-andanh/$toId/$fromId")
        latestMessagesToRef.setValue(chatMessage)
        Loading?.dismiss()
    }

    override fun didSearchTerm(term: String) {}

    override fun onDismissed(selectedContentType: GPHContentType) {}

    override fun onGifSelected(
        media: Media,
        searchTerm: String?,
        selectedContentType: GPHContentType
    ) {
        performsendMessage(media.images.fixedWidth?.gifUrl.toString())
    }
}


class ChatFromItem(
    val text: String,
    val time: String,
    val user: SignUpActivity.getUser,
    val Photo: String
) :
    Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        if (Photo == "") {
            viewHolder.itemView.textviewfrom_chat_from_row.isVisible = true
            viewHolder.itemView.GuiAnhFrom.isVisible = false
        } else {
            viewHolder.itemView.textviewfrom_chat_from_row.isVisible = false
            viewHolder.itemView.GuiAnhFrom.isVisible = true
            Glide.with(context).load(Photo).into(viewHolder.itemView.GuiAnhFrom)
        }
        viewHolder.itemView.textviewfrom_chat_from_row.text = text
        viewHolder.itemView.Txt_time_From.text = time

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(
    val text: String,
    val time: String,
    val user: SignUpActivity.getUser,

    val Photo: String
) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        if (Photo == "") {
            viewHolder.itemView.textviewfrom_chat_to_row.isVisible = true
            viewHolder.itemView.GuiAnhTo.isVisible = false
        } else {
            viewHolder.itemView.textviewfrom_chat_to_row.isVisible = false
            viewHolder.itemView.GuiAnhTo.isVisible = true
            Glide.with(context).load(Photo).into(viewHolder.itemView.GuiAnhTo)
        }
        viewHolder.itemView.textviewfrom_chat_to_row.text = text
        viewHolder.itemView.Txt_time_To.text = time
        Picasso.get().load(R.drawable.andanh).into(viewHolder.itemView.imageViewchat_to_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}