package my.illrock.a1codechallenge.presentation.view.maintypes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.MainTypesRepository
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import my.illrock.a1codechallenge.presentation.view.util.ViewModelResult
import my.illrock.a1codechallenge.util.getOrAwaitValue
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class MainTypesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val mainTypesRepository = mockk<MainTypesRepository> {
        coEvery {
            get(MOCK_MANUFACTURER_ID)
        } returns ResultWrapper.Success(mainTypesMock())
    }

    private lateinit var vm: MainTypesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = MainTypesViewModel(mainTypesRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun loadMainTypes_success_forced() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, true)
            assertEquals(
                mainTypesMock(),
                vm.getSuccess().data
            )
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_success_notForced() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(
                mainTypesMock(),
                vm.getSuccess().data
            )
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_success_forcedReplacesOld() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(
                mainTypesMock(),
                vm.getSuccess().data
            )

            coEvery {
                mainTypesRepository.get(MOCK_MANUFACTURER_ID)
            } returns ResultWrapper.Success(anotherMainTypesMock())

            vm.loadMainTypes(MOCK_MANUFACTURER_ID, true)
            assertEquals(
                anotherMainTypesMock(),
                vm.getSuccess().data
            )
        }

        coVerify(exactly = 2) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_success_notForcedDoesNotReplaceOld() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(
                mainTypesMock(),
                vm.getSuccess().data
            )

            coEvery {
                mainTypesRepository.get(MOCK_MANUFACTURER_ID)
            } returns ResultWrapper.Success(anotherMainTypesMock())
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(
                mainTypesMock(),
                vm.getSuccess().data
            )
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_success_twice_empty() {
        coEvery {
            mainTypesRepository.get(MOCK_MANUFACTURER_ID)
        } returns ResultWrapper.Success(listOf())

        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)

            //We have empty mainTypes set, and although it's not forced call, we will reload from network
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertTrue(vm.getSuccess().data.isEmpty())
        }

        //Check that we used network twice
        coVerify(exactly = 2) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_error_noData() {
        coEvery {
            mainTypesRepository.get(MOCK_MANUFACTURER_ID)
        } returns ResultWrapper.Error(NoDataException())

        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, true)
            assertEquals(R.string.error_no_data, vm.getError().errorRes)
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_error_noInternet() {
        coEvery {
            mainTypesRepository.get(MOCK_MANUFACTURER_ID)
        } returns ResultWrapper.Error(UnknownHostException())

        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, true)
            assertEquals(R.string.error_connection, vm.getError().errorRes)
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun loadMainTypes_error_custom() {
        val expectedMessage = "Woah, what's wrong?"
        val expectedException = Exception(expectedMessage)
        coEvery {
            mainTypesRepository.get(MOCK_MANUFACTURER_ID)
        } returns ResultWrapper.Error(expectedException)

        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, true)
            assertEquals(expectedMessage, vm.getError().errorMessage)
        }

        coVerify(exactly = 1) { mainTypesRepository.get(any()) }
    }

    @Test
    fun startSearch() {
        vm.startSearch()
        assertTrue(vm.isSearch.getOrAwaitValue())
    }

    @Test
    fun stopSearch() {
        vm.stopSearch()
        assertFalse(vm.isSearch.getOrAwaitValue())
    }

    @Test
    fun stopSearch_resetsMainTypes() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(mainTypesMock(), vm.getSuccess().data)

            vm.startSearch()
            vm.onNewSearchInput("5")
            assertEquals(
                listOf(MainType(MOCK_MAIN_TYPE_2, MOCK_MAIN_TYPE_2)),
                vm.getSuccess().data
            )

            vm.stopSearch()
            assertEquals(mainTypesMock(), vm.getSuccess().data)
        }
    }

    @Test
    fun onNewSearchInput() {
        testDispatcher.runBlockingTest {
            vm.loadMainTypes(MOCK_MANUFACTURER_ID, false)
            assertEquals(mainTypesMock(), vm.getSuccess().data)

            vm.startSearch()
            vm.onNewSearchInput("l")
            assertEquals(
                listOf(MainType(MOCK_MAIN_TYPE_1, MOCK_MAIN_TYPE_1)),
                vm.getSuccess().data
            )
        }
    }

    private fun MainTypesViewModel.getSuccess() =
        result.getOrAwaitValue() as ViewModelResult.Success

    private fun MainTypesViewModel.getError() =
        result.getOrAwaitValue() as ViewModelResult.Error

    private fun mainTypesMock() = listOf(
        MainType(MOCK_MAIN_TYPE_1, MOCK_MAIN_TYPE_1),
        MainType(MOCK_MAIN_TYPE_2, MOCK_MAIN_TYPE_2),
    )

    private fun anotherMainTypesMock() = listOf(
        MainType(MOCK_MAIN_TYPE_3, MOCK_MAIN_TYPE_3),
    )

    private companion object {
        const val MOCK_MANUFACTURER_ID = 303L
        const val MOCK_MAIN_TYPE_1 = "XL3"
        const val MOCK_MAIN_TYPE_2 = "XXS5"
        const val MOCK_MAIN_TYPE_3 = "L1"
    }
}