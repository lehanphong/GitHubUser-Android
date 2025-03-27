package ntd.molea.githubuser.utils

import android.util.Log

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun e(throwable: Throwable) {
        Log.e("ERROR", throwable.message, throwable)
    }
} 