package pers.zhc.android.qrcodetransfer

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import pers.zhc.android.qrcodetransfer.databinding.ActivitySendBinding
import pers.zhc.android.qrcodetransfer.utils.toast

class SendActivity: AppCompatActivity() {
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
    }

    private fun onScanned(content: String) {
        toast(content)
    }
}
