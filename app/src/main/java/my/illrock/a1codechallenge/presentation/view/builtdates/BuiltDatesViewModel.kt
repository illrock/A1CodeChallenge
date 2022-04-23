package my.illrock.a1codechallenge.presentation.view.builtdates

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
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.BuiltDatesRepository
import my.illrock.a1codechallenge.data.repository.exception.NoDataException
import my.illrock.a1codechallenge.presentation.view.util.ViewModelResult
import my.illrock.a1codechallenge.util.print
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class BuiltDatesViewModel @Inject constructor(
    private val repository: BuiltDatesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _result = MutableLiveData<ViewModelResult<List<BuiltDate>>>()
    val result: LiveData<ViewModelResult<List<BuiltDate>>> = _result

    fun loadBuiltDates(manufacturerId: Long, mainTypeId: String, isForce: Boolean) {
        _result.value = ViewModelResult.Loading
        viewModelScope.launch(dispatcher) {
            repository.get(manufacturerId, mainTypeId, isForce).let { result ->
                withContext(Dispatchers.Main) {
                    handleBuiltDatesResult(result)
                }
            }
        }
    }

    private fun handleBuiltDatesResult(result: ResultWrapper<List<BuiltDate>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _result.value = ViewModelResult.Success(result.data)
            }
            is ResultWrapper.Error -> showError(result.exception)
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

    private fun LiveData<ViewModelResult<List<BuiltDate>>>.isNotEmptySuccess(): Boolean {
        return (value as? ViewModelResult.Success)?.data?.isNotEmpty()
            ?: false
    }
}