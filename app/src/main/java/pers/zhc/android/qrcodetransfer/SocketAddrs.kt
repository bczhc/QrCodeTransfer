package pers.zhc.android.qrcodetransfer

object SocketAddrs {
    val targetAddr: String
        get() = Config.read()?.send?.target ?: ""

    val listenOn: String
        get() = Config.read()?.receive?.listenOn ?: ""
}
