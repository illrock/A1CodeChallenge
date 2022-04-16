package my.illrock.a1codechallenge.presentation.view.manufacturer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import my.illrock.a1codechallenge.data.model.Manufacturer
import my.illrock.a1codechallenge.data.repository.ManufacturersRepository
import my.illrock.a1codechallenge.util.print
import javax.inject.Inject

@HiltViewModel
class ManufacturersViewModel @Inject constructor(
    private val repository: ManufacturersRepository,
) : ViewModel() {
    private lateinit var _manufacturersFlow: Flow<PagingData<Manufacturer>>
    val manufacturersFlow: Flow<PagingData<Manufacturer>>
        get() = _manufacturersFlow

    init {
        loadManufacturers()
    }

    private fun loadManufacturers() {
        viewModelScope.launch {
            try {
                repository.get().cachedIn(viewModelScope).let { flow ->
                    _manufacturersFlow = flow
                }
            } catch (e: Exception) {
                e.print()
                //todo show error
            }
        }
    }
}