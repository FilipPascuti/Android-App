package com.ilazar.myapp.todo.items

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.todo.data.Item
import com.ilazar.myapp.todo.data.local.TodoDatabase
import com.ilazar.myapp2.todo.data.ItemRepository
import kotlinx.coroutines.launch

class ItemsViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableItems = MutableLiveData<List<Item>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Throwable>().apply { value = null }

    val items: LiveData<List<Item>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Throwable> = mutableException

    val itemRepository: ItemRepository

    init {
        val itemDao = TodoDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = ItemRepository(itemDao, application.applicationContext)
        items = itemRepository.items
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            val result = itemRepository.refresh()
            Log.d(TAG, "fetched items ${result.getOrNull()}");
            if (result.isSuccess) {
                Log.d(TAG, "refresh succeeded this is the items data: ${items.value}");
            }
            if (result.isFailure) {
                Log.w(TAG, "refresh failed", result.exceptionOrNull());
                mutableException.value = result.exceptionOrNull()
            }

            mutableLoading.value = false
        }
    }
}
