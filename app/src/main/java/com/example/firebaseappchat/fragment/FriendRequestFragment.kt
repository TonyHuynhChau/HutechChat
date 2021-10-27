package fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.R
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_thong_bao.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FriendRequestFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friendsrequest, container, false)
        recyclerView = view.findViewById(R.id.Notification_Friends_Request)
        AutoLoad()
        return view
    }

    private fun AutoLoad() {
        FirebaseDatabase.getInstance().getReference("FriendsRequest")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("CHẠYYYYYYYYYYY","Được")
                    val userNguoiDung = FirebaseAuth.getInstance().currentUser
                    val adapter = GroupAdapter<GroupieViewHolder>()
                    val FirebaseDb = FirebaseDatabase.getInstance().getReference("FriendsRequest")
                    if (userNguoiDung != null) {
                        FirebaseDb.child("Nhận ${userNguoiDung.uid}")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach() {
                                        val user = it.getValue(SignUpActivity.getUser::class.java)!!
                                        adapter.add(UItem(user))
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("Error: ", error.message)
                                }
                            })
                        recyclerView.adapter = adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    class UItem(val user: SignUpActivity.getUser) : Item<GroupieViewHolder>() {
        val FriendsRequest = FirebaseDatabase.getInstance().getReference("FriendsRequest")
        override fun getLayout(): Int {
            return R.layout.layout_thong_bao
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if (user.Urlphoto.isEmpty()) {
                val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                        "&pid=ImgRaw&r=0"
                viewHolder.itemView.Txtusername_Thong_Bao.text = user.name
                Picasso.get().load(ImgDefault).into(viewHolder.itemView.IVUser_Thong_Bao)
                Log.d("New Message :", "User Name : ${user.name} \n PhotoUrl : $ImgDefault")
            } else {
                viewHolder.itemView.Txtusername_Thong_Bao.text = user.name
                Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.IVUser_Thong_Bao)
                Log.d("New Message :", "User Name : ${user.name} \n PhotoUrl : ${user.Urlphoto}")
            }
            viewHolder.itemView.findViewById<Button>(R.id.BtnDongY).setOnClickListener {
                DongYFriends(user.uid)
            }
            viewHolder.itemView.findViewById<Button>(R.id.BtnTuchoi).setOnClickListener {
                HuyKetBan(user.uid)
            }
        }

        private fun HuyKetBan(receiverUserid: String) {
            val userNguoiDung = FirebaseAuth.getInstance().currentUser
            if (userNguoiDung != null) {
                FriendsRequest.child("Nhận ${userNguoiDung.uid}").child("Gửi $receiverUserid")
                    .removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                        }
                    }
                FriendsRequest.child("Gửi $receiverUserid")
                    .child("Nhận ${userNguoiDung.uid}")
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                        }
                    }
            }
        }

        fun DongYFriends(uid: String) {
            val userNguoiDung = FirebaseAuth.getInstance().currentUser
            if (userNguoiDung != null) {
                val Friends = FirebaseDatabase.getInstance().getReference("Friends")
                Friends.child(userNguoiDung.uid).child(uid).child("uid").setValue(uid)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Them Ban
                            Friends.child(uid).child(userNguoiDung.uid).child("uid")
                                .setValue(userNguoiDung.uid)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        FriendsRequest.child("Nhận " + userNguoiDung.uid)
                                            .child("Gửi " + uid)
                                            .removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(
                                                        "Message:",
                                                        "Đã Xóa Thành Công {Gửi ${userNguoiDung.uid}}"
                                                    )
                                                }
                                            }
                                        FriendsRequest.child("Gửi " + uid)
                                            .child("Nhận " + userNguoiDung.uid)
                                            .removeValue()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(
                                                        "Message:",
                                                        "Đã Xóa Thành Công {Gửi $uid}"
                                                    )
                                                }
                                            }
                                        FriendsRequest.child("Gửi " + userNguoiDung.uid)
                                            .child("Nhận " + uid)
                                            .removeValue().addOnCompleteListener(
                                                OnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d(
                                                            "Message:",
                                                            "Đã Xóa Thành Công {Gửi ${userNguoiDung.uid}}"
                                                        )
                                                    }
                                                })
                                        FriendsRequest.child("Nhận " + uid)
                                            .child("Gửi " + userNguoiDung.uid)
                                            .removeValue()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(
                                                        "Message:",
                                                        "Đã Xóa Thành Công {Gửi $uid}"
                                                    )
                                                }
                                            }
                                    }
                                }
                        }
                    }
            }
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