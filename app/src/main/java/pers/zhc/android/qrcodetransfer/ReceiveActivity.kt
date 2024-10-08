package pers.zhc.android.qrcodetransfer

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pers.zhc.android.qrcodetransfer.databinding.ActivityReceiveBinding
import pers.zhc.android.qrcodetransfer.utils.QrCode

class ReceiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindings = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.responseBtn.setOnClickListener {
            bindings.setQrCode("Hello")
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

    companion object {
        private var lastBitmap: Bitmap? = null
    }
}
