package id.petersam.catatankeuangan.util

sealed class LoadState<out T> {
    data class Success<out Data>(val data: Data) : LoadState<Data>()
    data class Error(val e: Exception) : LoadState<Nothing>()
    object Loading : LoadState<Nothing>()
}