package pers.zhc.android.qrcodetransfer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pers.zhc.android.qrcodetransfer.databinding.ActivityReceiveBinding
import pers.zhc.android.qrcodetransfer.utils.QrCode
import pers.zhc.android.qrcodetransfer.utils.androidAssert
import pers.zhc.android.qrcodetransfer.utils.launchIo
import pers.zhc.android.qrcodetransfer.utils.withMain
import java.util.Date

class ReceiveActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityReceiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.responseBtn.setOnClickListener {
            bindings.setQrCode("Hello")
        }

        lifecycleScope.launchIo {
            startSocketListener()
        }

        onBackPressedDispatcher.addCallback {
            // go die, to finish the listening socket
            throw RuntimeException()
        }
    }

    private fun ActivityReceiveBinding.setQrCode(content: String?) {
        if (content == null) {
            qrWaitPlaceholder.visibility = View.VISIBLE
            qrIv.visibility = View.INVISIBLE
        } else {
            qrWaitPlaceholder.visibility = View.INVISIBLE
            qrIv.visibility = View.VISIBLE
            lastBitmap?.recycle()
            val bitmap = QrCode.generate(content)
            lastBitmap = bitmap
            qrIv.setImageBitmap(bitmap)
        }
    }

    private suspend fun startSocketListener() {
        coroutineScope {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket =
                aSocket(selectorManager).tcp().bind(SocketAddrs.listenOnHost, SocketAddrs.listenOnPort.toInt())
            logAppend("Listener started")
            while (true) {
                val socket = serverSocket.accept()
                logAppend("Accepted: $socket")
                launch {
                    runCatching {
                        handleConnection(socket)
                    }.onFailure {
                        logAppend("Socket error: $it")
                    }
                }
            }
        }
    }

    private suspend fun handleConnection(socket: Socket) {
        val readChannel = socket.openReadChannel()
        if (!readChannel.checkHeader()) {
            logAppend("Invalid header")
            return
        }

        while (true) {
            val message = readChannel.readMessage()
            androidAssert(message.type == MessageType.QRCODE.type)

            withMain { bindings.setQrCode(message.content) }
            logAppend("Update QR code at: ${Date()}")
        }
    }

    private suspend fun logAppend(line: String) {
        withMain {
            bindings.logTv.addLine(line)
        }
    }

    companion object {
        private var lastBitmap: Bitmap? = null
    }

    @SuppressLint("SetTextI18n")
    fun TextView.addLine(line: String) {
        this.text = "${this.text}$line\n"
    }
}
