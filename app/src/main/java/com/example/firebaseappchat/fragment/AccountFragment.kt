package fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.UserProfile
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //date
    var year = 0
    var month = 0
    var day = 0

    var saveyear = 0
    var savemonth = 0
    var saveday = 0

    //Lấy uid Của Người Dùng Từ Authentication gồm (Email,Pass,Display,.....)
    val userdata = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun readata(
        uid: String,
        avatar: ImageView,
        txtPassWord: TextView,
        name2: TextView,
        email: TextView,
        TxtSex_Profile: TextView,
        txtPhone_Profile: TextView
    ) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val emailU = dataSnapshot.child("$uid/email").value
                val name = dataSnapshot.child("$uid/name").value
                val date = dataSnapshot.child("$uid/Date").value
                val phonenumber = dataSnapshot.child("$uid/Phone").value
                val Sex = dataSnapshot.child("$uid/Sex").value
                val photo = dataSnapshot.child("$uid/Urlphoto").value
                if (photo == null) {
                    Picasso.get().load(IMGURL).into(avatar)
                } else {
                    Picasso.get().load(photo.toString()).into(avatar)
                }
                txtPassWord.text = date.toString()
                name2.text = name.toString()
                email.text = emailU.toString()
                TxtSex_Profile.text = Sex.toString()
                txtPhone_Profile.text = phonenumber.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("loadPost:onCancelled", error.toException())
            }
        }
        database.addValueEventListener(postListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_account, container, false)
        val btnUpdate: Button = view.findViewById(R.id.btnSua)
        val avatar: ImageView = view.findViewById(R.id.avatar2)
        val txtPassWord: TextView = view.findViewById(R.id.txtPassWord)
        val name: TextView = view.findViewById(R.id.name2)
        val email: TextView = view.findViewById(R.id.email2)
        val TxtSex_Profile: TextView = view.findViewById(R.id.TxtSex_Profile)
        val txtPhone_Profile: TextView = view.findViewById(R.id.txtPhone_Profile)
        if (userdata != null) {
            if (userdata.photoUrl != null) {
                readata(
                    userdata.uid,
                    avatar,
                    txtPassWord,
                    name,
                    email,
                    TxtSex_Profile,
                    txtPhone_Profile
                )
            }
        }
        btnUpdate.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, UserProfile::class.java)
            startActivity(intent)
        })
        return view
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}