package pers.zhc.android.qrcodetransfer

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pers.zhc.android.qrcodetransfer.databinding.ActivitySendBinding
import pers.zhc.android.qrcodetransfer.utils.createProgressDialog
import pers.zhc.android.qrcodetransfer.utils.launchIo
import pers.zhc.android.qrcodetransfer.utils.toast
import pers.zhc.android.qrcodetransfer.utils.withMain

class SendActivity : AppCompatActivity() {
    private lateinit var writeChannel: ByteWriteChannel
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) {
        it.contents ?: run {
            // cancelled
            finish()
            return@registerForActivityResult
        }
        onScanned(it.contents)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindings = ActivitySendBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.scanBtn.setOnClickListener {
            barcodeLauncher.launch(ScanOptions().apply {
                this.setBeepEnabled(false)
                this.setOrientationLocked(false)
            })
        }

        val progressDialog = createProgressDialog("连接中……").also { it.show() }

        lifecycleScope.launchIo {
            runCatching {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val socket =
                    aSocket(selectorManager).tcp().connect(SocketAddrs.targetHost, SocketAddrs.targetPort.toInt())
                val readChannel = socket.openReadChannel()
                writeChannel = socket.openWriteChannel(autoFlush = true).also {
                    it.writeFully(HEADER)
                }
                launch {
                    runCatching {
                        while (true) {
                            toast(readChannel.readMessage().type)
                        }
                    }
                }
            }.onFailure {
                toast("连接失败")
                withMain { bindings.scanBtn.isEnabled = false }
            }
            withMain { progressDialog.dismiss() }
        }
    }

    private fun onScanned(content: String) {
        val dialog = createProgressDialog("发送中……").also { it.show() }
        lifecycleScope.launchIo {
            writeChannel.writeMessage(Message(MessageType.QRCODE.type, content))
            withMain { dialog.dismiss() }
        }
    }
}
