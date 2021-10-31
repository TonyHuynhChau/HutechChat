package com.example.firebaseappchat.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.SearchUser.SearchUserActivity
import com.example.firebaseappchat.VideoCall.VideoChatActivity
import com.example.firebaseappchat.databinding.ActivityMainBinding
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fragment.AccountFragment
import fragment.DashboardFragment
import fragment.FriendRequestFragment
import fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        var currentUser: SignUpActivity.getUser? = null
    }

    private lateinit var binding: ActivityMainBinding

    //Fragment
    private val dashboardFragment = DashboardFragment()
    private val homeFragment = HomeFragment()
    private val accountFragment = AccountFragment()
    private val friendFragment = FriendRequestFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Hutech Chat"
        fecthCurrentUser()
        verifyUserLoggedIn()

        val user = FirebaseAuth.getInstance().currentUser
        //Bottom nav
        replaceFrag(homeFragment)
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFrag(homeFragment)
                R.id.notification_friend_request->replaceFrag(friendFragment)
                R.id.dashboard -> replaceFrag(dashboardFragment)
                R.id.account -> replaceFrag(accountFragment)
            }
            true
        }
    }
    private fun fecthCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(SignUpActivity.getUser::class.java)
                Log.d("lastes mesager", "current user ${currentUser?.Urlphoto}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun replaceFrag(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                startActivity(Intent(this, SearchUserActivity::class.java))
            }

            R.id.menu_new_mess -> {
                startActivity(Intent(this, NewMessActivity::class.java))
            }

            R.id.menu_new_call -> {
                startActivity(Intent(this, VideoChatActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
