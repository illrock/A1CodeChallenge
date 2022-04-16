package my.illrock.a1codechallenge.presentation.view.manufacturer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.illrock.a1codechallenge.data.network.response.ResultWrapper
import my.illrock.a1codechallenge.data.network.response.manufacturer.ManufacturersResponse
import my.illrock.a1codechallenge.data.repository.ManufacturerRepository
import my.illrock.a1codechallenge.util.print
import javax.inject.Inject

@HiltViewModel
class ManufacturerViewModel @Inject constructor(
    private val repository: ManufacturerRepository
) : ViewModel() {
    private val _manufacturers = MutableLiveData<Map<Int, String>>()
    val manufacturers: LiveData<Map<Int, String>> = _manufacturers

    fun loadManufacturers() {
        viewModelScope.launch {
            repository.get(1, 15).let { result ->
                withContext(Dispatchers.Main) {
                    handleManufacturersResult(result)
                }
            }
        }
    }

    private fun handleManufacturersResult(result: ResultWrapper<ManufacturersResponse>) {
        when (result) {
            is ResultWrapper.Success -> {
                val wkda = result.data.wkda
                _manufacturers.value = wkda
            }
            is ResultWrapper.Error -> {
                result.exception.print()
                //todo show error
            }
        }
    }
}