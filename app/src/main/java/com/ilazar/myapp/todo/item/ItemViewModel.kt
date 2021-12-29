package com.ilazar.myapp.todo.item

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.todo.data.Item
import com.ilazar.myapp.todo.data.local.TodoDatabase
import com.ilazar.myapp2.todo.data.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableItem = MutableLiveData<Item>().apply { value = Item("", "", "", 0, false) }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Throwable>().apply { value = null }

    val item: LiveData<Item> = mutableItem
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Throwable> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted
    var setLiked: Boolean = false

    val itemRepository: ItemRepository

    init {
        val itemDao = TodoDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = ItemRepository(itemDao, application.applicationContext)
    }


    fun getItemById(itemId: String): LiveData<Item> {
        Log.v(TAG, "getItemById...")
        return itemRepository.getById(itemId)
    }

    fun didSetLiked() {
        setLiked = true
    }

    fun saveOrUpdateItem(_id: String?, text: String, date: String, length: Int, liked: Boolean) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...");
//            val item = mutableItem.value ?: return@launch
//
//            item.text = text
//            item.date = date
//            item.length = length
//            item.liked = liked

            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Item>
            if (_id != null && _id != "") {
                val item = Item(_id, text, date, length, liked)
                result = itemRepository.update(item)
            } else {
                val item = Item("", text, date, length, liked)
                result = itemRepository.save(item)
            }
            if (result.isSuccess) {
                Log.d(TAG, "saveOrUpdateItem succeeded");
//                mutableItem.value = result.getOrNull()
            }
            if (result.isFailure) {
                Log.w(TAG, "saveOrUpdateItem failed", result.exceptionOrNull());
                mutableException.value = result.exceptionOrNull()
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}
