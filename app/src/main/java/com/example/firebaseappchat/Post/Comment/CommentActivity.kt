package com.example.firebaseappchat.Post.Comment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.Comments
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_allcomment.view.*
import java.text.SimpleDateFormat
import java.util.*


class CommentActivity : AppCompatActivity(), GiphyDialogFragment.GifSelectionListener {

    private lateinit var EmojiButton: ImageView
    var selectPhotoUrl: Uri? = null
    lateinit var GuiAnh: ImageView
    lateinit var GIF: ImageView
    private var Loading: ProgressDialog? = null

    private lateinit var txtComment: EditText
    private lateinit var recyclerComment: RecyclerView
    private lateinit var btnSendComment: ImageView

    private lateinit var PostKey: String
    private lateinit var current_user_id: String
    private lateinit var RandomKey: String

    private lateinit var usersRef: DatabaseReference
    private lateinit var postRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        supportActionBar?.hide()

        txtComment = findViewById(R.id.input_comment)
        EmojiButton = findViewById(R.id.btnEmojiComment)
        recyclerComment = findViewById(R.id.recyclerViewComment)
        btnSendComment = findViewById(R.id.btnSendComment)
        GIF = findViewById(R.id.btnGIFComment)
        GuiAnh = findViewById(R.id.btnAnhComment)

        PostKey = intent.getStringExtra("Key").toString()

        mAuth = FirebaseAuth.getInstance()
        current_user_id = mAuth.currentUser?.uid.toString()
        usersRef = FirebaseDatabase.getInstance().reference.child("User")
        postRef = FirebaseDatabase.getInstance().reference.child("Post").child(PostKey)

        //Emoji
        EmojiManager.install(GoogleEmojiProvider())
        val popup =
            EmojiPopup.Builder.fromRootView(findViewById(R.id.rootViewComment)).build(txtComment)
        EmojiButton.setOnClickListener {
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

        //Send Comment
        btnSendComment.setOnClickListener {
            validateComment()
            txtComment.setText("")
        }

        AutoLoad(recyclerComment)
    }

    //Add Images and Gif
//    private fun updateImages() {
//        if (selectPhotoUrl != null) {
//            val filename = UUID.randomUUID().toString()
//            val ref = FirebaseStorage.getInstance().getReference("ImagesMessage/$filename")
//            ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
//                //Lấy URL Của Ảnh
//                ref.downloadUrl.addOnSuccessListener {
//                    performsendMessage(it.toString())
//                }.addOnFailureListener {
//                    Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            selectPhotoUrl = data.data
//            updateImages()
//        }
//    }

    //Add Comment
    @SuppressLint("SimpleDateFormat")
    private fun validateComment() {
        val commentText = txtComment.text.toString()

        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Hãy Viết Gì Đó Đi...", Toast.LENGTH_SHORT).show()
        } else {
            val calDate = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd MMM yyyy")
            val saveCurrentDate = currentDate.format(calDate.time)

            val calTime = Calendar.getInstance()
            val currentTime = SimpleDateFormat("hh:mm:ss")
            val saveCurrentTime = currentTime.format(calDate.time)

            val userName = mAuth.currentUser?.displayName.toString()

            var commentMap = mapOf<String, String>(
                "uid" to current_user_id,
                "comment" to commentText,
                "date" to saveCurrentDate,
                "time" to saveCurrentTime,
                "name" to userName
            )
            postRef.child("comment").child(current_user_id + saveCurrentDate + saveCurrentTime)
                .updateChildren(commentMap)
                .addOnSuccessListener { task ->
                    Toast.makeText(this, "Comment Thành Công", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //Load Comment
    fun AutoLoad(recyclerviewComments: RecyclerView) {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LayPost(recyclerviewComments)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun LayPost(recyclerviewComments: RecyclerView) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        val ref =
            FirebaseDatabase.getInstance().getReference("Post").child(PostKey).child("comment")
        ref.orderByChild("date")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val user = it.getValue(Comments::class.java)
                    if (user != null) {
                        Log.d("user", user.uid)
                        FindIMGUSER(user, adapter)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        recyclerviewComments.adapter = adapter
        recyclerviewComments.scrollToPosition(adapter.itemCount - 1)
    }

    private fun FindIMGUSER(user: Comments, adapter: GroupAdapter<GroupieViewHolder>) {
        FirebaseDatabase.getInstance().getReference("user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val AnhProfile_Post =
                        snapshot.child("${user.uid}/Urlphoto").value.toString()
                    Log.d("AnhProfile_Post", AnhProfile_Post)
                    adapter.add(UItem(user, AnhProfile_Post))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    class UItem(val user: Comments, val Anh_Profile_Post: String) :
        Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.layout_allcomment
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if (Anh_Profile_Post.isEmpty() || Anh_Profile_Post == "null" || Anh_Profile_Post == "") {
                Picasso.get().load(IMGURL).into(viewHolder.itemView.comment_profile_image)
            } else {
                Picasso.get().load(Anh_Profile_Post).into(viewHolder.itemView.comment_profile_image)
            }
            viewHolder.itemView.comment_username.text = user.name
            viewHolder.itemView.comment_text.text = user.comment
            viewHolder.itemView.comment_date.text = user.date
            viewHolder.itemView.comment_time.text = user.time
            if (user.Urlphoto.isEmpty()) {
                viewHolder.itemView.comment_image.isVisible = false
            } else {
                Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.comment_image)
            }
        }
    }


    //Don't Care
    override fun didSearchTerm(term: String) {
        TODO("Not yet implemented")
    }

    override fun onDismissed(selectedContentType: GPHContentType) {
        TODO("Not yet implemented")
    }

    override fun onGifSelected(
        media: Media,
        searchTerm: String?,
        selectedContentType: GPHContentType
    ) {
        TODO("Not yet implemented")
    }

}


