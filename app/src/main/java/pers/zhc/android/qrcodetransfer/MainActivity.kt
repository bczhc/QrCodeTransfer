package pers.zhc.android.qrcodetransfer

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import pers.zhc.android.qrcodetransfer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.sendBtn.setOnClickListener {
            startActivity(Intent(this, SendActivity::class.java))
            saveConfig()
        }
        bindings.receiveBtn.setOnClickListener {
            startActivity(Intent(this, ReceiveActivity::class.java))
            saveConfig()
        }

        bindings.targetConfigEt.setText(SocketAddrs.targetAddr)
        bindings.listenOnConfigEt.setText(SocketAddrs.listenOn)

        onBackPressedDispatcher.addCallback {
            saveConfig()
            finish()
        }
    }

    private fun saveConfig() {
        val config = Config(
            send = SendingConfig(bindings.targetConfigEt.text.toString()),
            receive = ReceivingConfig(bindings.listenOnConfigEt.text.toString())
        )
        Config.write(config)
    }

    override fun onPause() {
        super.onPause()
        saveConfig()
    }
}
