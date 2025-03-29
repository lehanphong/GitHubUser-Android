package ntd.molea.githubuser.data.remote

sealed class ApiException(message: String) : Exception(message) {
    class ServerError(code: Int) : ApiException("Server error occurred: $code")
    class ClientError(code: Int) : ApiException("Client error occurred: $code")
    class NetworkError : ApiException("Network connection error")
    class UnknownError : ApiException("Unknown error occurred")
} 