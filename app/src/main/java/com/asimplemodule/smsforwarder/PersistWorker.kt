package com.asimplemodule.smsforwarder

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class PersistWorker(val context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    companion object {
        private const val TAG = "PersistWorker"
    }

    override fun doWork(): Result {
        val args = inputData
        val data = args.keyValueMap
            .filterKeys { IncommingMessagesRepoContants.COLS.contains(it) }
            .mapValues { it.toString() }

        Log.d(TAG,"Data was converted to map")
        Log.d(TAG,"Data of first col is ${data.get(IncommingMessagesRepoContants.COL_API_KEY)}")
                

        val repo = IncommingMessagesRepo(context)
        repo.insert(data)


        return Result.success()

    }
}