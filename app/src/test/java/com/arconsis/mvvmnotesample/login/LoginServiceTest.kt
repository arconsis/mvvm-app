package com.arconsis.mvvmnotesample.login

import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.UserResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response


/**
 * Created by Alexander on 16.05.2017.
 */
class LoginServiceTest {
    @Test
    fun successfulLogin() {
        val loginApi = mock<LoginApi>()
        whenever(loginApi.login(any(), any())).thenReturn(Single.just(Response.success(UserResponse(200, 0))))


        val loginService = LoginService(Schedulers.trampoline(), Schedulers.trampoline(), loginApi)
        loginService.login("a", "a").test().assertResult(Result.success(User(0, "a", "a")))
    }

    @Test
    fun failedLogin() {
        val loginApi = mock<LoginApi>()
        whenever(loginApi.login(any(), any())).thenReturn(Single.just(Response.error(500, ResponseBody.create(MediaType.parse("application/json"), "failure"))))

        val loginService = LoginService(Schedulers.trampoline(), Schedulers.trampoline(), loginApi)
        loginService.login("a", "a").test().assertResult(Result.failure())
    }
}