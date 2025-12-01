package com.example.tiendastore.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.tiendastore.data.repository.UsuarioRepository
import com.example.tiendastore.model.Usuario
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var application: Application
    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        application = mockk(relaxed = true)
        repository = mockk()

        // Mock default behavior for init block (checkHealth)
        coEvery { repository.checkHealth() } returns true
        
        viewModel = AuthViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates currentUser and clears errors`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val user = Usuario(1L, "Test User", email, password, "USER", "", "")

        coEvery { repository.login(email, password) } returns Result.success(user)

        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(user, viewModel.currentUser.value)
        assertTrue(viewModel.ui.value.errors.isEmpty())
    }

    @Test
    fun `login failure updates ui message`() = runTest {
        val email = "test@example.com"
        val password = "wrongpassword"

        coEvery { repository.login(email, password) } returns Result.failure(Exception("Error"))

        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Credenciales inválidas", viewModel.ui.value.message)
    }
}
