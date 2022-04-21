package my.illrock.a1codechallenge.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.MainTypesResponse
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
class MainTypesRepositoryTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private val apiService = mockk<ApiService> {
        coEvery { getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(mapOf())
    }

    private lateinit var repository: MainTypesRepository

    @Before
    fun setUp() {
        repository = MainTypesRepository(apiService, testDispatcher)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun get_apiSuccess() {
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(mapOf(
            MOCK_TYPE_1 to MOCK_TYPE_1,
            MOCK_TYPE_2 to MOCK_TYPE_2
        ))

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }

        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
    }

    @Test
    fun get_apiNoData() {
        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertTrue(actualException is NoDataException)
        }

        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
    }

    @Test
    fun get_apiError() {
        val expectedException = Exception("What a terrible failure")
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } throws expectedException

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertEquals(expectedException, actualException)
        }

        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
    }

    private companion object {
        const val MOCK_MANUFACTURER_ID = 101L
        const val MOCK_TYPE_1 = "X7"
        const val MOCK_TYPE_2 = "i4"
    }
}