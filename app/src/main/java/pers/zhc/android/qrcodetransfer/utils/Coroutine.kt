package pers.zhc.android.qrcodetransfer.utils

import kotlinx.coroutines.*

fun CoroutineScope.launchIo(
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(Dispatchers.IO, block = block)
}

suspend fun <R> withIo(
    block: CoroutineScope.() -> R
): R {
    return withContext(Dispatchers.IO, block = block)
}

suspend fun <R> withMain(
    block: CoroutineScope.() -> R
): R {
    return withContext(Dispatchers.Main, block = block)
}
