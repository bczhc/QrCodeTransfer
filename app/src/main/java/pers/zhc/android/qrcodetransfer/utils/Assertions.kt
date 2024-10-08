package pers.zhc.android.qrcodetransfer.utils

fun androidAssert(condition: Boolean) {
    if (!condition) throw AssertionError("Assertion failed")
}
