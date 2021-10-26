package fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.Friend.Profile_Other_User_Activity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_thong_bao.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendRequestFragment : Fragment() {
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
    private lateinit var recyclervew:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friendsrequest, container, false)
         recyclervew = view.findViewById(R.id.Notification_Friends_Request)
        RequesrFriend()
        return view
    }

    private fun RequesrFriend() {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        val FirebaseDb = FirebaseDatabase.getInstance().getReference("/FriendsRequest")
        if (userNguoiDung != null) {
            FirebaseDb.child("Nhận ${userNguoiDung.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val adapter = GroupAdapter<GroupieViewHolder>()
                        snapshot.children.forEach() {
                            val user = it.getValue(SignUpActivity.getUser::class.java)!!
                            adapter.add(UItem(user))

                        }
                        adapter.setOnItemClickListener { item, view ->

                            val userItem = item as UItem

                            val intent =
                                Intent(view.context, Profile_Other_User_Activity::class.java)
                            Log.d("New Message", userItem.user.name)
                            intent.putExtra("USER_KEY", userItem.user)
                            startActivity(intent)
                        }
                        recyclervew.adapter = adapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error: ", error.message)
                    }
                })
        }
    }

    class UItem(val user: SignUpActivity.getUser) : Item<GroupieViewHolder>() {
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