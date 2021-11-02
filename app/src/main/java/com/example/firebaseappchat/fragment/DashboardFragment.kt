package fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.Post.ClickPost
import com.example.firebaseappchat.Post.PostActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.model.Post
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.layou_post.view.*
import java.sql.Ref
import java.text.SimpleDateFormat

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    var LikeChecker: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val NguoiDung = FirebaseAuth.getInstance().currentUser
        if (NguoiDung != null) {
            if (NguoiDung.photoUrl == null) {
                Picasso.get().load(IMGURL).into(view.ImgUser)
            } else {
                Picasso.get().load(NguoiDung.photoUrl).into(view.ImgUser)
            }
        }
        val BtnPost: TextView = view.findViewById(R.id.TxtDangBai)
        BtnPost.setOnClickListener {
            val intent = Intent(view.context, PostActivity::class.java)
            startActivity(intent)
        }
        AutoLoad(view.recyclerView_Post)
        return view
    }

    fun AutoLoad(recyclerviewPost: RecyclerView) {
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LayPost(recyclerviewPost)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun LayPost(recyclerviewPost: RecyclerView) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("Post")
        ref.orderByChild("date")
        ref.orderByChild("time")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val user = it.getValue(Post::class.java)
                    var AnhProfile_Post = ""
                    if (user != null) {
                        FirebaseDatabase.getInstance().getReference("user").child(user.uid)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    AnhProfile_Post = snapshot.child("Urlphoto").value.toString()
                                    adapter.add(
                                        0, UItem(user, AnhProfile_Post, LikeChecker)
                                    )
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val NguoiDung = FirebaseAuth.getInstance().currentUser
                    val userItem = item as UItem
                    if (NguoiDung != null) {
                        if (userItem.user.uid == NguoiDung.uid) {
                            val intent = Intent(view.context, ClickPost::class.java)
                            intent.putExtra(NewMessActivity.USER_KEY, userItem.user)
                            intent.putExtra("Anh Nguoi Dung", userItem.Anh_Profile_Post)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        recyclerviewPost.adapter = adapter
    }


    class UItem(val user: Post, val Anh_Profile_Post: String, var LikeChecker: Boolean) :
        Item<GroupieViewHolder>() {
        val LikeRef = FirebaseDatabase.getInstance().getReference("Likes")
        val NguoiDungID = FirebaseAuth.getInstance().uid
        var CountLike = 0

        override fun getLayout(): Int {
            return R.layout.layou_post
        }

        fun DemLuotLike(viewHolder: GroupieViewHolder) {
            val PostKey = user.uid + user.date + user.time
            val LikeRef = FirebaseDatabase.getInstance().getReference("Likes")
            LikeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(PostKey).hasChild(NguoiDungID.toString())) {
                        CountLike = snapshot.child(PostKey).childrenCount.toInt()
                        viewHolder.itemView.Like.setImageResource(R.drawable.like)
                        viewHolder.itemView.TxtLike.text = CountLike.toString() + "Like"
                    } else {
                        CountLike = snapshot.child(PostKey).childrenCount.toInt()
                        viewHolder.itemView.Like.setImageResource(R.drawable.dislike)
                        viewHolder.itemView.TxtLike.text = CountLike.toString() + "Like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            AutoDemLuotLike(viewHolder)
            if (Anh_Profile_Post.isEmpty()) {
                val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                        "&pid=ImgRaw&r=0"
                Picasso.get().load(ImgDefault).into(viewHolder.itemView.post_profile_image)
            } else {
                Picasso.get().load(Anh_Profile_Post).into(viewHolder.itemView.post_profile_image)
            }
            viewHolder.itemView.post_username.text = user.name
            viewHolder.itemView.post_description.text = user.status
            viewHolder.itemView.post_date.text = " " + user.date
            viewHolder.itemView.post_time.text = "-" + user.time
            if (user.Urlphoto.isEmpty()) {
                viewHolder.itemView.post_image.isVisible = false
            } else {
                Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.post_image)
            }

            viewHolder.itemView.Like.setOnClickListener {
                val PostKey = user.uid + user.date + user.time
                LikeChecker = true

                LikeRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (LikeChecker.equals(true)) {
                            if (snapshot.child(PostKey).hasChild(NguoiDungID.toString())) {
                                LikeRef.child(PostKey).child(NguoiDungID.toString()).removeValue()
                                LikeChecker = false
                            } else {
                                LikeRef.child(PostKey).child(NguoiDungID.toString()).setValue(true)
                                LikeChecker = false
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }

        private fun AutoDemLuotLike(viewHolder: GroupieViewHolder) {
            val LikeRef = FirebaseDatabase.getInstance().getReference("Likes")
            LikeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    DemLuotLike(viewHolder)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



