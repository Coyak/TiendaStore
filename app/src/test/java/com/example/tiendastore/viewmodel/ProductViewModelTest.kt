package com.example.tiendastore.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.tiendastore.data.repository.ProductoRepository
import com.example.tiendastore.model.Producto
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
class ProductViewModelTest {

    private lateinit var application: Application
    private lateinit var repository: ProductoRepository
    private lateinit var viewModel: ProductViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchProducts updates products state`() = runTest {
        val p1 = Producto(1L, "P1", "Desc", 100.0, 10, "")
        coEvery { repository.listarProductos() } returns Result.success(listOf(p1))

        viewModel = ProductViewModel(application, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.products.value.size)
        assertEquals("P1", viewModel.products.value[0].nombre)
    }

    @Test
    fun `addOrUpdate validates form before saving`() = runTest {
        viewModel = ProductViewModel(application, repository)
        
        // Empty form
        viewModel.addOrUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        // Should have errors
        assertTrue(viewModel.form.value.errors.isNotEmpty())
        assertEquals("Requerido", viewModel.form.value.errors["name"])
    }
}
