package my.illrock.a1codechallenge.data.repository

import android.text.format.DateUtils
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeDao
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity
import my.illrock.a1codechallenge.data.network.ApiService
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.MainTypesResponse
import my.illrock.a1codechallenge.data.provider.SystemClockProvider
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
    private val mainTypeDao = mockk<MainTypeDao> {
        coEvery { getByManufacturerId(MOCK_MANUFACTURER_ID) } returns listOf()
        coJustRun { deleteByManufacturerId(MOCK_MANUFACTURER_ID) }
        coJustRun { insert(any()) }
    }
    private val systemClockProvider = mockk<SystemClockProvider> {
        coEvery { elapsedRealtime() } returns MOCK_TIME
    }

    private lateinit var repository: MainTypesRepository

    @Before
    fun setUp() {
        repository = MainTypesRepository(apiService, mainTypeDao, testDispatcher, systemClockProvider)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun get_force_apiSuccess() {
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(mainTypesMock())

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, true)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }

        coVerify(exactly = 1) { mainTypeDao.deleteByManufacturerId(MOCK_MANUFACTURER_ID) }
        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
        coVerify(exactly = 1) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 1) { mainTypeDao.insert(entitiesMock()) }
        coVerify(exactly = 0) { mainTypeDao.getByManufacturerId(any()) }
    }

    @Test
    fun get_force_apiNoData() {
        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, true)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertTrue(actualException is NoDataException)
        }

        coVerify(exactly = 1) { mainTypeDao.deleteByManufacturerId(MOCK_MANUFACTURER_ID) }
        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
        coVerify(exactly = 0) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { mainTypeDao.getByManufacturerId(any()) }
        coVerify(exactly = 0) { mainTypeDao.insert(any()) }
    }

    @Test
    fun get_force_apiError() {
        val expectedException = Exception("What a terrible failure")
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } throws expectedException

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, true)
            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertEquals(expectedException, actualException)
        }

        coVerify(exactly = 1) { mainTypeDao.deleteByManufacturerId(MOCK_MANUFACTURER_ID) }
        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
        coVerify(exactly = 0) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { mainTypeDao.getByManufacturerId(any()) }
        coVerify(exactly = 0) { mainTypeDao.insert(any()) }
    }

    @Test
    fun get_noForce_outdated_apiSuccess() {
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(mainTypesMock())
        coEvery { mainTypeDao.getByManufacturerId(MOCK_MANUFACTURER_ID) } returns entitiesMock()
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }

        coVerify(exactly = 1) { mainTypeDao.deleteByManufacturerId(MOCK_MANUFACTURER_ID) }
        coVerify(exactly = 1) { apiService.getMainTypes(any()) }
        coVerify(exactly = 2) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 1) { mainTypeDao.insert(entitiesNewTimeMock()) }
        coVerify(exactly = 1) { mainTypeDao.getByManufacturerId(MOCK_MANUFACTURER_ID) }
    }

    @Test
    fun get_noForce_notOutdated_dbSuccess() {
        coEvery { mainTypeDao.getByManufacturerId(MOCK_MANUFACTURER_ID) } returns entitiesMock()
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_NOT_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }

        coVerify(exactly = 1) { mainTypeDao.getByManufacturerId(MOCK_MANUFACTURER_ID) }
        coVerify(exactly = 0) { mainTypeDao.deleteByManufacturerId(any()) }
        coVerify(exactly = 0) { apiService.getMainTypes(any()) }
        coVerify(exactly = 1) { systemClockProvider.elapsedRealtime() }
        coVerify(exactly = 0) { mainTypeDao.insert(any()) }
    }

    @Test
    fun get_api_sortByName() {
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(
            mapOf(
                MOCK_TYPE_2 to MOCK_TYPE_2,
                MOCK_TYPE_1 to MOCK_TYPE_1
            )
        )

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, true)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }
    }

    @Test
    fun get_db_sortByName() {
        coEvery { apiService.getMainTypes(MOCK_MANUFACTURER_ID) } returns MainTypesResponse(
            mapOf(
                MOCK_TYPE_2 to MOCK_TYPE_2,
                MOCK_TYPE_1 to MOCK_TYPE_1
            )
        )
        coEvery { systemClockProvider.elapsedRealtime() } returns MOCK_NOT_OUTDATED_TIME

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_MANUFACTURER_ID, false)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_TYPE_1, data[0].id)
            assertEquals(MOCK_TYPE_1, data[0].name)
            assertEquals(MOCK_TYPE_2, data[1].id)
            assertEquals(MOCK_TYPE_2, data[1].name)
        }
    }

    private fun mainTypesMock() = mapOf(
        MOCK_TYPE_1 to MOCK_TYPE_1,
        MOCK_TYPE_2 to MOCK_TYPE_2
    )

    private fun entitiesMock() = listOf(
        MainTypeEntity(MOCK_TYPE_1, MOCK_TYPE_1, MOCK_MANUFACTURER_ID, MOCK_TIME),
        MainTypeEntity(MOCK_TYPE_2, MOCK_TYPE_2, MOCK_MANUFACTURER_ID, MOCK_TIME),
    )

    private fun entitiesNewTimeMock() = listOf(
        MainTypeEntity(MOCK_TYPE_1, MOCK_TYPE_1, MOCK_MANUFACTURER_ID, MOCK_OUTDATED_TIME),
        MainTypeEntity(MOCK_TYPE_2, MOCK_TYPE_2, MOCK_MANUFACTURER_ID, MOCK_OUTDATED_TIME),
    )

    private companion object {
        const val MOCK_MANUFACTURER_ID = 101L
        const val MOCK_TYPE_1 = "X7"
        const val MOCK_TYPE_2 = "i4"

        const val MOCK_TIME = 1000L
        const val MOCK_OUTDATED_TIME = MOCK_TIME + DateUtils.HOUR_IN_MILLIS + 1
        const val MOCK_NOT_OUTDATED_TIME = MOCK_TIME + DateUtils.HOUR_IN_MILLIS - 1
    }
}