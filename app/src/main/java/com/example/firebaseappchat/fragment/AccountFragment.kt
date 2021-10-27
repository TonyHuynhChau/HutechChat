package fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.firebaseappchat.R
import com.example.firebaseappchat.databinding.DialogFriendRequestBinding
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.model.UserProfile
import com.example.firebaseappchat.model.UserProfile.Companion.IMGURL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_friend_request.*
import kotlinx.android.synthetic.main.fragment_account.*
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

        if (userdata != null) {
            if (userdata.photoUrl != null) {
                readata(userdata.uid)
            }
        }
    }

    private fun readata(uid: String) {
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
                    Picasso.get().load(IMGURL).into(avatar2)
                } else {
                    Picasso.get().load(photo.toString()).into(avatar2)
                }
                txtPassWord.text = Editable.Factory.getInstance().newEditable(date.toString())
                name2.text = Editable.Factory.getInstance().newEditable(name.toString())
                email2.text = Editable.Factory.getInstance().newEditable(emailU.toString())
                TxtSex_Profile.text = Editable.Factory.getInstance().newEditable(Sex.toString())
                txtPhone_Profile.text =
                    Editable.Factory.getInstance().newEditable(phonenumber.toString())
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