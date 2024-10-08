package pers.zhc.android.qrcodetransfer.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator

fun Context.createProgressDialog(title: String): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(CircularProgressIndicator(this).apply {
            isIndeterminate = true
        }).create()
}
