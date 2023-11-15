package eu.mcomputing.mobv.zadanie.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.mcomputing.mobv.zadanie.data.api.DataRepository

class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Tu môžete vykonávať asynchrónnu prácu
        Log.d("MyWorker", "spustenie workera")
        DataRepository.getInstance(applicationContext).apiGeofenceUsers()

        return Result.success()
    }
}