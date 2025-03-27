package ntd.molea.githubuser.utils

class MockLogger : Logger {
    private val logs = mutableListOf<LogEntry>()

    data class LogEntry(
        val type: LogType,
        val tag: String?,
        val message: String?,
        val throwable: Throwable?
    )

    enum class LogType {
        DEBUG, ERROR
    }

    override fun d(tag: String, message: String) {
        logs.add(LogEntry(LogType.DEBUG, tag, message, null))
    }

    override fun e(throwable: Throwable) {
        logs.add(LogEntry(LogType.ERROR, null, null, throwable))
    }

    fun getLogs(): List<LogEntry> = logs.toList()

    fun clear() {
        logs.clear()
    }
} 