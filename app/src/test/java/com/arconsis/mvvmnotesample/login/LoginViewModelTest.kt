package com.arconsis.mvvmnotesample.login

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.login.vm.LoginViewModel
import com.arconsis.mvvmnotesample.login.vm.ProcessingState
import com.arconsis.mvvmnotesample.login.vm.ProcessingStateChangedEvent
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncRepository
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito


/**
 * Created by Alexander on 15.05.2017.
 */
class LoginViewModelTest {
    @get:Rule val rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun successfulLogin() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.success(User(0, "a", "a"))))
        val actions = mock<LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.processingState.observe(getTestLifecycleOwner(), createObserver(actions))

        vm.username = "a"
        vm.password = "a"
        vm.doLogin()

        verify(actions, Mockito.times(1)).login(Mockito.notNull())
        verify(actions, Mockito.times(1)).processing()
        verify(actions, Mockito.times(0)).failed()
    }

    @Test
    fun failedLogin() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.failure()))
        val actions = mock<LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.processingState.observe(getTestLifecycleOwner(), createObserver(actions))

        vm.username = "a"
        vm.password = "a"
        vm.doLogin()

        verify(actions, Mockito.times(0)).login(Mockito.any())
        verify(actions, Mockito.times(1)).processing()
        verify(actions, Mockito.times(1)).failed()
    }

    @Test
    fun notifyActionsOnSetWhenUserIsPresent() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        val actions = mock<LoginActions>()
        val vm = LoginViewModel(User(0, "a", "a"), loginService, notesSyncRepository)
        vm.processingState.observe(getTestLifecycleOwner(), createObserver(actions))

        verify(actions, Mockito.times(1)).login(Mockito.notNull())
        verify(actions, Mockito.times(0)).processing()
        verify(actions, Mockito.times(0)).failed()
        verify(actions, Mockito.times(0)).dataMissing()
    }

    @Test
    fun notifyOnDataMissing() {
        val loginService = mock<LoginService>()
        val notesSyncRepository = mock<NotesSyncRepository>()
        whenever(loginService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(Result.failure()))
        val actions = mock<LoginActions>()

        val vm = LoginViewModel(null, loginService, notesSyncRepository)
        vm.processingState.observe(getTestLifecycleOwner(), createObserver(actions))

        vm.doLogin()

        verify(actions, Mockito.times(1)).dataMissing()
        verify(actions, Mockito.times(0)).failed()
        verify(actions, Mockito.times(0)).processing()
    }

    private fun createObserver(loginActions: LoginActions): Observer<ProcessingStateChangedEvent<User>> {
        return Observer { event ->
            val state = event?.state
            when (state) {
                ProcessingState.Processing -> loginActions.processing()
                ProcessingState.DataMissing -> loginActions.dataMissing()
                ProcessingState.Failed -> loginActions.failed()
                ProcessingState.Login -> loginActions.login(event.data)
                null -> loginActions.nothing()
            }
        }
    }

    private interface LoginActions {
        fun processing()
        fun dataMissing()
        fun failed()
        fun nothing()
        fun login(data: User?)
    }

    private fun getTestLifecycleOwner(): LifecycleOwner {
        return LifecycleOwner {
            val lifecycle = mock<Lifecycle>()
            whenever(lifecycle.currentState).thenReturn(Lifecycle.State.RESUMED)
            lifecycle
        }
    }
}
