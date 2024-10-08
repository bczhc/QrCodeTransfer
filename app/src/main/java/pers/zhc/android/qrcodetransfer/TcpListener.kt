package pers.zhc.android.qrcodetransfer

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

class TcpListener {
    val serverSocket: ServerSocket

    init {
        val selectorManager = SelectorManager(Dispatchers.IO)
        serverSocket = aSocket(selectorManager).tcp().bind(SocketAddrs.listenOnHost, SocketAddrs.listenOnPort.toInt())
    }
}
