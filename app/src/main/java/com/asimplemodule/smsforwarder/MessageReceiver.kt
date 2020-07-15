package com.asimplemodule.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.work.*
import java.util.concurrent.TimeUnit

class MessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        if(Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent!!.action)){
            val enqueWork = workEnquer(context)

            val receivedMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            receivedMessages.forEach { enqueWork(createDataFromMessage(it)) }
        }
    }

    fun workEnquer(context:Context): (Data)-> Unit {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        return fun (messageToSend: Data): Unit {
            val parseAndSendRequest = OneTimeWorkRequestBuilder<ParseAndSendWorker>()
            .setInputData(messageToSend)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
                .build()

            val persistWorker = OneTimeWorkRequestBuilder<PersistWorker>()
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
                .addTag(Constants.WORKER_TAG)
                .build()


            WorkManager.getInstance(context)
                .beginWith(parseAndSendRequest)
                .then(persistWorker).enqueue()
        }

    }

    fun createDataFromMessage(message:SmsMessage): Data {
        return workDataOf(
            Constants.END_POINT to BuildConfig.LOGGER_ENDPOINT,
            Constants.PHONE_NUMBER to message.displayOriginatingAddress,
            Constants.MESAGE_BODY to message.displayMessageBody,
            Constants.TIMESTAMP to message.timestampMillis
        )
    }
}