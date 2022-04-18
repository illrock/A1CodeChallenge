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
import my.illrock.a1codechallenge.data.model.BuiltDate
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.BuiltDatesRepository
import javax.inject.Inject

@HiltViewModel
class BuiltDatesViewModel @Inject constructor(
    private val repository: BuiltDatesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _builtDates = MutableLiveData<List<BuiltDate>>(listOf())
    val builtDates: LiveData<List<BuiltDate>> = _builtDates

    fun loadBuiltDates(manufacturerId: Long, mainTypeId: String, isForce: Boolean) {
        if (!isForce && !builtDates.value.isNullOrEmpty()) return

        viewModelScope.launch(dispatcher) {
            repository.get(manufacturerId, mainTypeId).let { result ->
                withContext(Dispatchers.Main) {
                    handleBuiltDatesResult(result)
                }
            }
        }
    }

    private fun handleBuiltDatesResult(result: ResultWrapper<List<BuiltDate>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _builtDates.value = result.data
            }
            is ResultWrapper.Error -> {
                //todo show error
            }
        }
    }
}