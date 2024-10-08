package pers.zhc.android.qrcodetransfer

import android.content.Context
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import pers.zhc.android.qrcodetransfer.databinding.ActivityReceiveBinding
import pers.zhc.android.qrcodetransfer.utils.*
import java.text.SimpleDateFormat
import java.util.*

class ReceiveActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityReceiveBinding
    private var writeChannel: ByteWriteChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.responseBtn.setOnClickListener {
            val writeChannel = writeChannel ?: run {
                toast("无连接。")
                return@setOnClickListener
            }
            val dialog = createProgressDialog("正在发送……").also { it.show() }
            lifecycleScope.launchIo {
                runCatching {
                    writeChannel.writeMessage(Message(MessageType.RESPONSE.type, ""))
                    logAppend("Response sent")
                }.onFailure {
                    logAppend("Response sending error: $it")
                }
                withMain { dialog.dismiss() }
            }
        }

        lifecycleScope.launchIo {
            startSocketListener()
        }

        onBackPressedDispatcher.addCallback {
            // go die, to finish the listening socket
            throw RuntimeException()
        }

        bindings.ipTv.text = getWifiIpString(this)
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
                runCatching {
                    handleConnection(socket)
                }.onFailure {
                    it.printStackTrace()
                    logAppend("Socket error: $it")
                }
            }
        }
    }

    private suspend fun handleConnection(socket: Socket) {
        val readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = true)
        if (!readChannel.checkHeader()) {
            logAppend("Invalid header")
            return
        }

        while (true) {
            val message = readChannel.readMessage()
            androidAssert(message.type == MessageType.QRCODE.type)

            withMain { bindings.setQrCode(message.content) }
            logAppend("Received content: ${message.content}")
        }
    }

    private val logDateFormatter by lazy { SimpleDateFormat("[HH:mm:ss]", Locale.getDefault()) }

    private suspend fun logAppend(line: String) {
        val logDate = logDateFormatter.format(Date())
        withMain {
            bindings.logEt.apply {
                append("$logDate $line\n")
                setSelection(text.length)
            }
        }
    }

    companion object {
        private var lastBitmap: Bitmap? = null

        @Suppress("DEPRECATION")
        fun getWifiIpString(context: Context): String {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipAddress = wm.connectionInfo.ipAddress
            return String.format(
                Locale.getDefault(),
                "%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff
            )
        }
    }
}
