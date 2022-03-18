package com.haybakubo.gamebirich88

import android.os.Bundle
import android.os.CountDownTimer
import com.dakulangsakalam.customwebview.jump_code.presentation.JumpActivity
import com.dakulangsakalam.customwebview.jump_code.presentation.utils.writeLogs


class SplashActivity : JumpActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val timer = object: CountDownTimer(3000, 1000){
            override fun onTick(p0: Long) { }
            override fun onFinish() {
                splashAction(true, 2) { version, downUrl ->
                    writeLogs("Version: $version \n Url: $downUrl")
                    startActivity(MainActivity.createIntent(this@SplashActivity))
                }
            }
        }
        timer.start()
    }
}