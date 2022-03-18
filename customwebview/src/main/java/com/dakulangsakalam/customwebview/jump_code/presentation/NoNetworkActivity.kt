package com.dakulangsakalam.customwebview.jump_code.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dakulangsakalam.customwebview.R

class NoNetworkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)
        setTheme(R.style.Theme_JumpCode)
    }

    companion object{
        fun createIntent(context: Context): Intent = Intent(context, NoNetworkActivity::class.java)
    }
}