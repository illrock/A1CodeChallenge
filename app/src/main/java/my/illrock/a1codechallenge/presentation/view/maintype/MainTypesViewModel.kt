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
import my.illrock.a1codechallenge.data.model.MainType
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.repository.MainTypesRepository
import my.illrock.a1codechallenge.util.print
import javax.inject.Inject

@HiltViewModel
class MainTypesViewModel @Inject constructor(
    private val repository: MainTypesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _mainTypes = MutableLiveData<List<MainType>>(listOf())
    val mainTypes: LiveData<List<MainType>> = _mainTypes

    fun loadMainTypes(manufacturerId: Long, isForce: Boolean) {
        if (isForce || mainTypes.value.isNullOrEmpty()) {
            viewModelScope.launch(dispatcher) {
                repository.get(manufacturerId).let { result ->
                    withContext(Dispatchers.Main) {
                        handleMainTypesResult(result)
                    }
                }
            }
        }
    }

    private fun handleMainTypesResult(result: ResultWrapper<List<MainType>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _mainTypes.value = result.data
            }
            is ResultWrapper.Error -> {
                //todo show error
                result.exception.print()
            }
        }
    }
}