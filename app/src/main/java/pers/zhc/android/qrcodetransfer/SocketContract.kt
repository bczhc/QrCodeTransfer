package pers.zhc.android.qrcodetransfer

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import java.nio.charset.StandardCharsets
import kotlin.text.toByteArray

data class Message(
    val type: String,
    val content: String,
)

enum class MessageType(
    val type: String,
) {
    QRCODE("qr-code-content"),
    RESPONSE("response"),
}

val HEADER = "bczhc-qrcode-transfer".toByteArray()

suspend fun ByteReadChannel.checkHeader(): Boolean {
    HEADER.forEach {
        if (this.readByte() != it) {
            return false
        }
    }
    return true
}

suspend fun ByteReadChannel.readMessage(): Message {
    val length = this.readInt(ByteOrder.BIG_ENDIAN)
    val buf = ByteArray(length)
    this.readFully(buf)
    val jsonString = String(buf, StandardCharsets.UTF_8)
    return App.GSON.fromJson(jsonString, Message::class.java)
}

suspend fun ByteWriteChannel.writeMessage(message: Message) {
    val json = App.GSON.toJson(message)
    this.writeInt(json.length, ByteOrder.BIG_ENDIAN)
    this.writeStringUtf8(json)
}
