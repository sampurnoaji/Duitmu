package id.petersam.dhuwite.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor() : ViewModel(){

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date> get() = _date

    fun onDateChanged(timeInMillis: Long) {
        val selectedDate = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }.time
        _date.value = selectedDate
    }
}