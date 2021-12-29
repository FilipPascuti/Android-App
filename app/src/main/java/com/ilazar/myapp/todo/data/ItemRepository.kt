package com.ilazar.myapp2.todo.data

import android.content.Context
import android.content.ContextParams
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.work.*
import com.ilazar.myapp.core.TAG
import com.ilazar.myapp.todo.data.Item
import com.ilazar.myapp.todo.data.local.ItemDao
import com.ilazar.myapp.todo.data.remote.ItemApi


class ItemRepository(private val itemDao: ItemDao, val context: Context) {

    val items = itemDao.getAll()

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    suspend fun refresh(): Result<Boolean> {
        try {
            val items = ItemApi.service.find()
            for (item in items) {
                itemDao.insert(item)
            }
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    fun getById(itemId: String): LiveData<Item> {
        return itemDao.getById(itemId)
    }

    suspend fun save(item: Item): Result<Item> {
        try {
            val createdItem = ItemApi.service.create(item)
            itemDao.insert(createdItem)
            return Result.success(createdItem)
        } catch (e: Exception) {

            val myWork = OneTimeWorkRequest.Builder(SaveWorker::class.java)
                .setConstraints(constraints)
                .setInputData(
                    Data.Builder()
                        .putString("text", item.text)
                        .putString("date", item.date)
                        .putInt("length", item.length)
                        .putBoolean("liked", item.liked)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).apply {
                enqueue(myWork)
            }
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            val itemId = (1..5)
                .map { allowedChars.random() }
                .joinToString("")
            itemDao.insert(Item(itemId, item.text, item.date, item.length, item.liked))
            return Result.success(item)
        }
    }

    suspend fun update(item: Item): Result<Item> {
        try {
            val updatedItem = ItemApi.service.update(item._id, item)
            itemDao.update(updatedItem)
            return Result.success(updatedItem)
        } catch (e: Exception) {
            val myWork = OneTimeWorkRequest.Builder(UpdateWorker::class.java)
                .setConstraints(constraints)
                .setInputData(
                    Data.Builder()
                        .putString("_id", item._id)
                        .putString("text", item.text)
                        .putString("date", item.date)
                        .putInt("length", item.length)
                        .putBoolean("liked", item.liked)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).apply {
                enqueue(myWork)
            }
            itemDao.update(item)
            return Result.success(item)
        }
    }
}

class SaveWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.w("main app", "save worker with input data: $inputData");

        val item = Item(
            "", inputData.getString("text")!!, inputData.getString("date")!!,
            inputData.getInt("length", 0), inputData.getBoolean("liked", false)
        )

        ItemApi.service.create(item)

        return Result.success()
    }

}

class UpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.w("main app", "save worker with input data: $inputData");

        val item = Item(
            inputData.getString("_id")!!, inputData.getString("text")!!, inputData.getString("date")!!,
            inputData.getInt("length", 0), inputData.getBoolean("liked", false)
        )

        ItemApi.service.update(item._id, item)

        return Result.success()
    }

}