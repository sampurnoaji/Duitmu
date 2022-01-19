package id.petersam.duitmu.ui.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.petersam.duitmu.data.TransactionRepository
import id.petersam.duitmu.model.Transaction
import id.petersam.duitmu.util.LoadState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UpdateTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _trx = MutableLiveData<LoadState<Transaction>>()
    val trx: LiveData<LoadState<Transaction>> get() = _trx

    fun getTransaction(trxId: String) {
        _trx.value = LoadState.Loading
        viewModelScope.launch {
            try {
                _trx.value = LoadState.Success(repository.getTransaction(trxId))
            } catch (e: Exception) {
                _trx.value = LoadState.Error(e.message.orEmpty())
            }
        }
    }
}