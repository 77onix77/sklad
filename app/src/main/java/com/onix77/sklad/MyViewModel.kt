package com.onix77.sklad

import androidx.lifecycle.*

import kotlinx.coroutines.launch

class MyViewModel(private val repository: Repository) :ViewModel() {

    fun getCat() = repository.getCat()

    fun getNameEl(cat: String) = repository.getNameEl(cat)

    fun getAllDateHis(fromDate: String, toDate: String) = repository.getAllDateHis(fromDate, toDate)

    fun getCatDateHis(fromDate: String, toDate: String, cat: String) = repository.getCatDateHis(fromDate, toDate, cat)

    fun getElDateHis(fromDate: String, toDate: String, cat: String, el: String) = repository.getElDateHis(fromDate, toDate, cat, el)

    fun getEl(cat: String): LiveData<List<ElementDB>> = repository.getEl(cat).asLiveData()

    fun updateEl(elementDB: ElementDB) {
        viewModelScope.launch {
            repository.updateEl(elementDB)
        }
    }

    fun insertEl(item: ElementDB) {
        viewModelScope.launch {
            repository.insertEl(item)
        }
    }

    fun insertInHistory(item: EntryHistory) {
        viewModelScope.launch {
            repository.insertInHistory(item)
        }
    }

}

class MyViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}