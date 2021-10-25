package fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.firebaseappchat.PageProfile.ThongBaoActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.model.UserProfile
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        view.recyclerview_latest_messages.adapter = adapter
        view.recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(context ,DividerItemDecoration.VERTICAL))
        //testdata(view)
        ListenForlatesMessages(view)
        return  view

    }
    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LateMessagesRow(it))
        }
    }

    private  fun ListenForlatesMessages(view: View){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] =chatMessage
                refreshRecyclerViewMessages()

                //adapter.add(HomeFragment.LateMessagesRow(chatMessage))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] =chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    class LateMessagesRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.message_textview_latest_messages.text =chatMessage.text

            val chatPartnerId :String
            if(chatMessage.formId ==FirebaseAuth.getInstance().uid){

                chatPartnerId =chatMessage.toId
            }else{
                chatPartnerId = chatMessage.formId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/user/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue((SignUpActivity.getUser::class.java))

                    val targetImageView = viewHolder.itemView.imageview_lastest_messages
                    viewHolder.itemView.username_textView_latestmessage.text = user?.name
                    Picasso.get().load(user?.Urlphoto).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }
    private fun testdata(view: View) {
        //val adapter = GroupAdapter<GroupieViewHolder>()
        //adapter.add(HomeFragment.LateMessagesRow())
        //view.recyclerview_latest_messages.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}