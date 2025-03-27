package ntd.molea.githubuser.utils

import android.util.Log

object Vlog {
    val isShowLog = true

    fun d(tag: String, log: String) {
        if (isShowLog) {
            Log.d(tag, log)
        }
    }

    fun e(e: Throwable, mess: String = "") {
        if (isShowLog) {
            Log.e("ERROR", "mess: $mess", e)
        }
    }

    fun i(tag: String, log: String) {
        if (isShowLog) {
            Log.i(tag, log)
        }
    }
}