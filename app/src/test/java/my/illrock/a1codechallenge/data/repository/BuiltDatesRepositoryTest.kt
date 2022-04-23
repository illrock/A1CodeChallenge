package my.illrock.a1codechallenge.data.repository

import android.text.format.DateUtils
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateDao
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.BuiltDatesResponse
import my.illrock.a1codechallenge.data.provider.SystemClockProvider
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
    private val builtDateDao = mockk<BuiltDateDao> {
        coEvery { getByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns listOf()
        coJustRun { deleteByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coJustRun { insert(any()) }
    }
    private val systemClockProvider = mockk<SystemClockProvider> {
        coEvery { elapsedRealtime() } returns MOCK_TIME
    }

    private lateinit var repository: BuiltDatesRepository

    @Before
    fun setUp() {
        repository = BuiltDatesRepository(apiService, builtDateDao, testDispatcher, systemClockProvider)
    }

    @Test
    fun get_force_apiSuccess() {
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(builtDatesMock())

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, true)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }

        coVerify(exactly = 1) { builtDateDao.deleteByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 1) { builtDateDao.insert(entitiesMock()) }
        coVerify(exactly = 0) { builtDateDao.getByManufacturerAndMainTypeIds(any(), any()) }
    }

    @Test
    fun get_force_apiNoData() {
        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, true)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertTrue(actualException is NoDataException)
        }

        coVerify(exactly = 1) { builtDateDao.deleteByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 0) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { builtDateDao.insert(any()) }
        coVerify(exactly = 0) { builtDateDao.getByManufacturerAndMainTypeIds(any(), any()) }
    }

    @Test
    fun get_force_apiError() {
        val expectedException = Exception("Built dates were not built actually")
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } throws expectedException

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, true)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertEquals(expectedException, actualException)
        }

        coVerify(exactly = 1) { builtDateDao.deleteByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 0) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { builtDateDao.insert(any()) }
        coVerify(exactly = 0) { builtDateDao.getByManufacturerAndMainTypeIds(any(), any()) }
    }

    @Test
    fun get_noForce_outdated_apiSuccess() {
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(builtDatesMock())
        coEvery { builtDateDao.getByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns entitiesMock()
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }

        coVerify(exactly = 1) { builtDateDao.getByManufacturerAndMainTypeIds(any(), any()) }
        coVerify(exactly = 2) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 1) { builtDateDao.deleteByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) }
        coVerify(exactly = 1) { builtDateDao.insert(entitiesNewTimeMock()) }
    }

    @Test
    fun get_noForce_notOutdated_dbSuccess() {
        coEvery { builtDateDao.getByManufacturerAndMainTypeIds(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns entitiesMock()
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_NOT_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }

        coVerify(exactly = 1) { builtDateDao.getByManufacturerAndMainTypeIds(any(), any()) }
        coVerify(exactly = 1) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { builtDateDao.deleteByManufacturerAndMainTypeIds(any(), any()) }
        coVerify(exactly = 0) { apiService.getBuiltDates(any(), any()) }
        coVerify(exactly = 0) { builtDateDao.insert(any()) }
    }

    @Test
    fun get_api_sortByName() {
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(
            mapOf(
                MOCK_BUILT_DATE_2 to MOCK_BUILT_DATE_2,
                MOCK_BUILT_DATE_1 to MOCK_BUILT_DATE_1
            )
        )

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, true)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }
    }

    @Test
    fun get_db_sortByName() {
        coEvery { apiService.getBuiltDates(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID) } returns BuiltDatesResponse(
            mapOf(
                MOCK_BUILT_DATE_2 to MOCK_BUILT_DATE_2,
                MOCK_BUILT_DATE_1 to MOCK_BUILT_DATE_1
            )
        )
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_NOT_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BUILT_DATE_1, data[0].id)
            assertEquals(MOCK_BUILT_DATE_1, data[0].date)
            assertEquals(MOCK_BUILT_DATE_2, data[1].id)
            assertEquals(MOCK_BUILT_DATE_2, data[1].date)
        }
    }

    private fun builtDatesMock() = mapOf(
        MOCK_BUILT_DATE_1 to MOCK_BUILT_DATE_1,
        MOCK_BUILT_DATE_2 to MOCK_BUILT_DATE_2
    )

    private fun entitiesMock() = listOf(
        BuiltDateEntity(MOCK_BUILT_DATE_1, MOCK_BUILT_DATE_1, MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, MOCK_TIME),
        BuiltDateEntity(MOCK_BUILT_DATE_2, MOCK_BUILT_DATE_2, MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, MOCK_TIME)
    )

    private fun entitiesNewTimeMock() = listOf(
        BuiltDateEntity(MOCK_BUILT_DATE_1, MOCK_BUILT_DATE_1, MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, MOCK_OUTDATED_TIME),
        BuiltDateEntity(MOCK_BUILT_DATE_2, MOCK_BUILT_DATE_2, MOCK_MANUFACTURER_ID, MOCK_MAIN_TYPE_ID, MOCK_OUTDATED_TIME)
    )

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    private companion object {
        const val MOCK_MANUFACTURER_ID = 202L
        const val MOCK_MAIN_TYPE_ID = "8er"
        const val MOCK_BUILT_DATE_1 = "2015"
        const val MOCK_BUILT_DATE_2 = "2014"

        const val MOCK_TIME = 200L
        const val MOCK_OUTDATED_TIME = MOCK_TIME + DateUtils.HOUR_IN_MILLIS + 1
        const val MOCK_NOT_OUTDATED_TIME = MOCK_TIME + DateUtils.HOUR_IN_MILLIS - 1
    }
}