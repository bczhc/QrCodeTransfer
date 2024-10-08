package pers.zhc.android.qrcodetransfer

object SocketAddrs {
    val targetAddr: String
        get() = Config.read()?.send?.target ?: ""

    val listenOn: String
        get() = Config.read()?.receive?.listenOn ?: ""

    val listenOnHost: String
        get() = listenOn.split(":").first()

    val listenOnPort: Short
        get() = listenOn.split(":").last().toShort()

    val targetHost: String
        get() = targetAddr.split(":").first()

    val targetPort: Short
        get() = targetAddr.split(":").last().toShort()
}
