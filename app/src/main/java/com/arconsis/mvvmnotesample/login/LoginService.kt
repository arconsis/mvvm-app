package com.arconsis.mvvmnotesample.login

import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.UserResponse
import com.arconsis.mvvmnotesample.util.retrofit
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginService(private val observingScheduler: Scheduler) {

    val loginApi: LoginApi = retrofit().create(LoginApi::class.java)

    fun login(username: String, password: String): Single<Result<User>> {
        return Single.fromCallable { loginApi.login(username, password).execute() }
                .subscribeOn(Schedulers.io())
                .map { r -> mapUserIdToUserResult(r, username, password) { code -> code == 200 } }
                .observeOn(observingScheduler)
    }

    fun register(username: String, password: String): Single<Result<User>> {
        return Single.fromCallable { loginApi.register(username, password).execute() }
                .subscribeOn(Schedulers.io())
                .map { r -> mapUserIdToUserResult(r, username, password) { code -> code == 201 } }
                .observeOn(observingScheduler)
    }

    private fun mapUserIdToUserResult(response: Response<UserResponse>, username: String, password: String, checkResponseCode: (Int) -> Boolean): Result<User> {
        if (response.code() == 200) {
            val userResponse = response.body()
            if (checkResponseCode(userResponse.code)) {
                val user = User(userResponse.id, username, password)
                return Result.success(user)
            }
        }
        return Result.failure()
    }
}