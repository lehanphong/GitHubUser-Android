package ntd.molea.githubuser.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class GitHubInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json;charset=utf-8")
            .build()
        return chain.proceed(request)
    }
} 