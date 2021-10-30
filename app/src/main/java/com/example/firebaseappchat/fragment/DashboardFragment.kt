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
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.Post.PostActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.layou_post.view.*
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val BtnPost: Button = view.findViewById(R.id.button)
        BtnPost.setOnClickListener {
            val intent = Intent(view.context, PostActivity::class.java)
            startActivity(intent)
        }
        LayPost(view.recyclerView_Post)
        return view
    }

    @SuppressLint("SimpleDateFormat")
    private fun LayPost(recyclerviewPost: RecyclerView) {
        val sdf2 = SimpleDateFormat("hh:mm:ss")
        val adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("Post")
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
                                    adapter.add(0, UItem(user, AnhProfile_Post))
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as com.example.firebaseappchat.UItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    Log.d("New Message:", NewMessActivity.USER_KEY)
                    intent.putExtra(NewMessActivity.USER_KEY, userItem.user)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        recyclerviewPost.adapter = adapter
    }

    class UItem(val user: Post, val Anh_Profile_Post: String) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.layou_post
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

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
            Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.post_image)
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



