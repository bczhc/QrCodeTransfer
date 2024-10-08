package pers.zhc.android.qrcodetransfer

import pers.zhc.android.qrcodetransfer.App.Companion.APP_CONTEXT
import pers.zhc.android.qrcodetransfer.App.Companion.GSON
import java.io.File

data class Config(
    var send: SendingConfig?,
    var receive: ReceivingConfig?,
) {
    companion object {
        private val configFile by lazy {
            File(APP_CONTEXT.filesDir, "config.json").also {
                if (!it.exists()) {
                    it.writeText("{}")
                }
            }
        }

        fun write(config: Config) {
            configFile.writeText(GSON.toJson(config))
        }

        fun read(): Config? {
            return runCatching {
                GSON.fromJson(configFile.readText(), Config::class.java)
            }.getOrNull()
        }

        fun default(): Config {
            return Config(send = null, receive = null)
        }
    }
}

data class SendingConfig(
    var target: String?,
)

data class ReceivingConfig(
    var listenOn: String?,
)
