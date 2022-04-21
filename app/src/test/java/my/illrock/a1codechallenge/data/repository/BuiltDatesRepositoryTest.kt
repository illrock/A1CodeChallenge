package my.illrock.a1codechallenge.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.BuiltDatesResponse
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
class BuiltDatesRepositoryTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private val apiService = mockk<ApiService> {
        coEvery { getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(mapOf())
    }

    private lateinit var repository: BuiltDatesRepository

    @Before
    fun setUp() {
        repository = BuiltDatesRepository(apiService, testDispatcher)
    }

    @Test
    fun get_apiSuccess() {
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(mapOf(
            MOCK_BUILT_DATE_1 to MOCK_BUILT_DATE_1,
            MOCK_BUILT_DATE_2 to MOCK_BUILT_DATE_2
        ))

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }

        coVerify(exactly = 1) { apiService.getBuiltDates(any(), any()) }
    }

    @Test
    fun get_apiNoData() {
        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertTrue(actualException is NoDataException)
        }

        coVerify(exactly = 1) { apiService.getBuiltDates(any(), any()) }
    }

    @Test
    fun get_apiError() {
        val expectedException = Exception("Built dates were not built actually")
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } throws expectedException

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertEquals(expectedException, actualException)
        }

        coVerify(exactly = 1) { apiService.getBuiltDates(any(), any()) }
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    private companion object {
        const val MOCK_MANUFACTURER_ID = 202L
        const val MOCK_MAIN_TYPE_ID = "8er"
        const val MOCK_BUILT_DATE_1 = "2014"
        const val MOCK_BUILT_DATE_2 = "2015"
    }
}