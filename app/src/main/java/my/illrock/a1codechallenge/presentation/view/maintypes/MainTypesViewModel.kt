package my.illrock.a1codechallenge.presentation.view.maintypes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.R
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.MainTypesRepository
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import my.illrock.a1codechallenge.presentation.view.util.ViewModelResult
import my.illrock.a1codechallenge.util.print
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MainTypesViewModel @Inject constructor(
    private val repository: MainTypesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _result = MutableLiveData<ViewModelResult<List<MainType>>>()
    val result: LiveData<ViewModelResult<List<MainType>>> = _result

    private val _originalMainTypes = mutableListOf<MainType>()
    private val _isSearch = MutableLiveData(false)
    val isSearch: LiveData<Boolean> = _isSearch
    private var searchInput: String = ""

    fun loadMainTypes(manufacturerId: Long, isForce: Boolean) {
        _result.value = ViewModelResult.Loading
        viewModelScope.launch(dispatcher) {
            repository.get(manufacturerId, isForce).let { result ->
                withContext(Dispatchers.Main) {
                    handleMainTypesResult(result)
                }
            }
        }
    }

    private fun handleMainTypesResult(result: ResultWrapper<List<MainType>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _originalMainTypes.clear()
                _originalMainTypes.addAll(result.data)

                if (searchInput.isEmpty()) {
                    _result.value = ViewModelResult.Success(result.data)
                } else {
                    val searchedData = result.data.applySearchInput(searchInput)
                    _result.value = getSearchResult(searchedData)
                }
            }
            is ResultWrapper.Error -> {
                _originalMainTypes.clear()
                showError(result.exception)
            }
        }
    }

    private fun showError(e: Exception) {
        val vmError = when {
            e is NoDataException -> ViewModelResult.Error(errorRes = R.string.error_no_data)
            e is UnknownHostException -> ViewModelResult.Error(errorRes = R.string.error_connection)
            e.message != null -> ViewModelResult.Error(errorMessage = e.message)
            else -> ViewModelResult.Error(errorRes = R.string.error_unknown)
        }
        _result.value = vmError
        e.print()
    }

    fun startSearch() {
        _isSearch.value = true
    }

    fun stopSearch() {
        _isSearch.value = false
        searchInput = ""
        if (_originalMainTypes.isNotEmpty()) {
            _result.value = ViewModelResult.Success(_originalMainTypes)
        }
    }

    fun onNewSearchInput(input: String) {
        searchInput = input
        if (_originalMainTypes.isEmpty()) return
        val searchedTypes = _originalMainTypes
            .applySearchInput(input)
        if (isSearch.value == true) {
            _result.value = getSearchResult(searchedTypes)
        }
    }

    private fun getSearchResult(searchedTypes: List<MainType>) = if (searchedTypes.isEmpty()) {
        ViewModelResult.Error(errorRes = R.string.error_search_empty)
    } else {
        ViewModelResult.Success(searchedTypes)
    }

    private fun List<MainType>?.applySearchInput(input: String) = this?.let { list ->
        if (input.isEmpty()) {
            list
        } else {
            list.filter { it.name.contains(input, true) }
        }
    } ?: listOf()
}