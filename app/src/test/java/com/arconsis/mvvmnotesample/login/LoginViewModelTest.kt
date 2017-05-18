package com.arconsis.mvvmnotesample.login

import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.sync.NotesSyncRepository
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.fest.assertions.Assertions
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Alexander on 15.05.2017.
 */
class LoginViewModelTest {
    @Test
    fun successfulLogin() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.success(User(0, "a", "a"))))
        val actions = mock<LoginViewModel.LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.loginActions = actions

        vm.username = "a"
        vm.password = "a"
        vm.doLogin()

        Assertions.assertThat(vm.savedUser).isNotNull
        verify(actions, Mockito.times(1)).processing()
        verify(actions, Mockito.times(0)).onLoginFailed()
        verify(actions, Mockito.times(1)).onLoginSuccessful(any())
    }

    @Test
    fun failedLogin() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.failure()))
        val actions = mock<LoginViewModel.LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.loginActions = actions

        vm.username = "a"
        vm.password = "a"
        vm.doLogin()

        Assertions.assertThat(vm.savedUser).isNull()
        verify(actions, Mockito.times(1)).processing()
        verify(actions, Mockito.times(1)).onLoginFailed()
        verify(actions, Mockito.times(0)).onLoginSuccessful(any())
    }

    @Test
    fun notifyActionsOnSetWhenUserIsPresent() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        val actions = mock<LoginViewModel.LoginActions>()
        val vm = LoginViewModel(User(0, "a", "a"), loginService, notesSyncRepository)
        vm.loginActions = actions

        verify(actions, Mockito.times(1)).onLoginSuccessful(any())
    }

    @Test
    fun notifyOnDataMissing() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.failure()))
        val actions = mock<LoginViewModel.LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.loginActions = actions

        vm.doLogin()

        verify(actions, Mockito.times(1)).onDataMissing()
        verify(actions, Mockito.times(0)).onLoginSuccessful(any())
        verify(actions, Mockito.times(0)).onLoginFailed()
        verify(actions, Mockito.times(0)).processing()
    }
}
