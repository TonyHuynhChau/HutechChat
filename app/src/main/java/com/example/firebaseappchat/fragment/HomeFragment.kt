package fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.messages.LateMessagesRow
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.random.Random


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var swipere: SwipeRefreshLayout

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
        view.recyclerview_latest_messages.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        swipere = view.findViewById(R.id.swipeRefresh)
        swipere.setOnRefreshListener(this::refreshRecyclerViewMessages)
        ListenForlatesMessages(view)
        return view
    }


    val latestMessagesMap = HashMap<String, ChatMessage>()
    @SuppressLint("LogNotTimber")
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            Handler().postDelayed(Runnable {
                swipere.isRefreshing = false
            }, 4000)
            adapter.add(LateMessagesRow(it))
            adapter.setOnItemClickListener { item, view ->
                val row = item as LateMessagesRow
                val intent = Intent(view.context, ChatLogActivity::class.java)
                intent.putExtra(NewMessActivity.USER_KEY, row.chatPartnerUser)
                startActivity(intent)
            }
        }
    }

    private fun ListenForlatesMessages(view: View) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }


            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            @SuppressLint("LongLogTag")
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("THÔNG BÁO XÓA TIN NHẮN BÊN HOMEFRAGMENT:", "THÀNH CÔNG")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

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