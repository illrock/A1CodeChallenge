package my.illrock.a1codechallenge.presentation.view.util

sealed class ViewModelResult<out R> {
    object Loading : ViewModelResult<Nothing>()
    data class Success<out T>(val data: T) : ViewModelResult<T>()
    data class Error(
        val errorRes: Int? = null,
        val errorMessage: String? = null
    ) : ViewModelResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading -> "Loading"
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[errorRes=$errorRes, errorMessage=$errorMessage]"
        }
    }
}