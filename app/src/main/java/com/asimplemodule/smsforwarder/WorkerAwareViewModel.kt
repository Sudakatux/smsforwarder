package com.asimplemodule.smsforwarder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager

class WorkerAwareViewModel(application: Application): AndroidViewModel(application) {
    private val workManagerInstance: WorkManager = WorkManager.getInstance(application)

    internal val workStatus: LiveData<List<WorkInfo>> get() = workManagerInstance.getWorkInfosByTagLiveData(Constants.WORKER_TAG)
}