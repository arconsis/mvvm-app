package com.arconsis.mvvmnotesample.login

import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.UserResponse
import com.arconsis.mvvmnotesample.util.retrofit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginService {

    val loginApi: LoginApi = retrofit().create(LoginApi::class.java)

    fun login(username: String, password: String): Observable<Result<User>> {
        return Observable.fromCallable { loginApi.login(username, password).execute() }
                .subscribeOn(Schedulers.io())
                .map { r -> mapUserIdToUserResult(r, username, password) { code -> code == 200 } }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun register(username: String, password: String): Observable<Result<User>> {
        return Observable.fromCallable { loginApi.register(username, password).execute() }
                .subscribeOn(Schedulers.io())
                .map { r -> mapUserIdToUserResult(r, username, password) { code -> code == 201 } }
                .observeOn(AndroidSchedulers.mainThread())
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