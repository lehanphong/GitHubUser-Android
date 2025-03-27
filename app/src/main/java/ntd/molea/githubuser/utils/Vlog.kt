package ntd.molea.githubuser.utils

object Vlog {
    private var logger: Logger = AndroidLogger()

    fun setLogger(newLogger: Logger) {
        logger = newLogger
    }

    fun d(tag: String, message: String) {
        logger.d(tag, message)
    }

    fun e(throwable: Throwable) {
        logger.e(throwable)
    }
}