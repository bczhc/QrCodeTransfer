package pers.zhc.android.qrcodetransfer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pers.zhc.android.qrcodetransfer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.sendBtn.setOnClickListener {
            startActivity(Intent(this, SendActivity::class.java))
        }
        bindings.receiveBtn.setOnClickListener {
            startActivity(Intent(this, ReceiveActivity::class.java))
        }
    }
}
