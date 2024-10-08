package pers.zhc.android.qrcodetransfer

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.gson.Gson

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var APP_CONTEXT: Context
        val GSON by lazy {
            Gson()
        }
    }
}
