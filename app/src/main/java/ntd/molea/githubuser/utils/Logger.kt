package ntd.molea.githubuser.utils

interface Logger {
    fun d(tag: String, message: String)
    fun e(throwable: Throwable)
} 