package com.example.firebaseappchat.messages

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.SearchUser.SearchUserActivity
import com.example.firebaseappchat.VideoCall.VideoChatActivity
import com.example.firebaseappchat.databinding.ActivityMainBinding
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fragment.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        var currentUser: SignUpActivity.getUser? = null
    }

    private lateinit var binding: ActivityMainBinding

    //Fragment
    private val dashboardFragment = DashboardFragment()
    private val chatandanh = ChatAnDanhFragment()
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

        val btnDarkMode: Switch = findViewById(R.id.btnDarkMode)
        val user = FirebaseAuth.getInstance().currentUser
        //Bottom nav
        replaceFrag(homeFragment)
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFrag(homeFragment)
                R.id.chatandanh -> replaceFrag(chatandanh)
                R.id.notification_friend_request -> replaceFrag(friendFragment)
                R.id.dashboard -> replaceFrag(dashboardFragment)
                R.id.account -> replaceFrag(accountFragment)
            }
            true
        }
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPref", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("Night Mode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            btnDarkMode.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            btnDarkMode.isChecked = false
        }

        btnDarkMode.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (btnDarkMode.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("Night Mode", true)
                sharedPrefsEdit.apply()
                Toast.makeText(this, "Chuyển Thành Công", Toast.LENGTH_LONG).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("Night Mode", false)
                sharedPrefsEdit.apply()
                Toast.makeText(this, "Chuyển Thành Công", Toast.LENGTH_LONG).show()
            }
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
