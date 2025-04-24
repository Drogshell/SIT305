package com.trevin.myplaylists.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trevin.myplaylists.R
import com.trevin.myplaylists.data.UserRepo
import com.trevin.myplaylists.data.database.PlaylistDatabase
import com.trevin.myplaylists.databinding.ActivityMainBinding
import com.trevin.myplaylists.ui.fragments.LoginFragment
import com.trevin.myplaylists.ui.fragments.SignupFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var userRepo: UserRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val database = PlaylistDatabase.getInstance(applicationContext)
        userRepo = UserRepo.getInstance(database)

        binding.buttonLogin.setOnClickListener {
            LoginFragment().show(
                supportFragmentManager,
                "LOGIN_SHEET"
            )
        }
        binding.buttonSignup.setOnClickListener {
            SignupFragment().show(
                supportFragmentManager,
                "SIGNUP_SHEET"
            )
        }

    }

}