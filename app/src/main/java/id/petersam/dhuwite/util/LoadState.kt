package id.petersam.dhuwite.util

sealed class LoadState<out T> {
    data class Success<out Data>(val data: Data) : LoadState<Data>()
    data class Error(val msg: String) : LoadState<Nothing>()
    object Loading : LoadState<Nothing>()
}