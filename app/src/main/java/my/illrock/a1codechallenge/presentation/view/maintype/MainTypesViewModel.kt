package my.illrock.a1codechallenge.presentation.view.maintype

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
import my.illrock.a1codechallenge.util.print
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MainTypesViewModel @Inject constructor(
    private val repository: MainTypesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _mainTypes = MutableLiveData<List<MainType>>(listOf())
    val mainTypes: LiveData<List<MainType>> = _mainTypes

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorRes = MutableLiveData<Int?>(null)
    val errorRes: LiveData<Int?> = _errorRes

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadMainTypes(manufacturerId: Long, isForce: Boolean) {
        if (!isForce && !mainTypes.value.isNullOrEmpty()) return

        _isLoading.value = true
        viewModelScope.launch(dispatcher) {
            repository.get(manufacturerId).let { result ->
                withContext(Dispatchers.Main) {
                    handleMainTypesResult(result)
                }
            }
        }
    }

    private fun handleMainTypesResult(result: ResultWrapper<List<MainType>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _isLoading.value = false
                _mainTypes.value = result.data
                if (result.data.isEmpty()) {
                    _errorMessage.value = null
                    _errorRes.value = R.string.error_empty_response
                } else {
                    _errorMessage.value = null
                    _errorRes.value = null
                }
            }
            is ResultWrapper.Error -> {
                _isLoading.value = false
                showError(result.exception)
            }
        }
    }

    private fun showError(e: Exception) {
        when {
            e is UnknownHostException -> {
                _errorMessage.value = null
                _errorRes.value = R.string.error_connection
            }
            e.message != null -> {
                _errorRes.value = null
                _errorMessage.value = e.message
            }
            else -> {
                _errorMessage.value = null
                _errorRes.value = R.string.error_unknown
            }
        }
        e.print()
    }
}