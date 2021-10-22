package fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        //btnSua.setOnClickListener{
        //   startActivity(Intent(UserProfile(), UserProfile::class.java))
        //}

    }

    private fun readata(uid: String) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val emailU = dataSnapshot.child("$uid/E-mail").value
                val name = dataSnapshot.child("$uid/name").value
                val date = dataSnapshot.child("$uid/Date").value
                val phonenumber = dataSnapshot.child("$uid/Phone").value
                val Sex = dataSnapshot.child("$uid/Sex").value
                val photo = dataSnapshot.child("$uid/Urlphoto").value

                Picasso.get().load(photo.toString()).into(avatar2)
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
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_account, container, false)
        var btnUpdate: Button = view.findViewById(R.id.btnSua)
        btnUpdate.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, UserProfile::class.java)
            startActivity(intent)
        })
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
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