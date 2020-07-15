package com.asimplemodule.smsforwarder

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ParseAndSendWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
){
    companion object {
        private const val TAG = "ParseAndSendWorker"
    }

    override fun doWork(): Result {
        val args = inputData
        val endPoint = args.getString(Constants.END_POINT);
        val phoneNumber = args.getString(Constants.PHONE_NUMBER)
        val messageBody = args.getString(Constants.MESAGE_BODY)
        val timeStamp = args.getLong(Constants.TIMESTAMP,0)


        Log.d(TAG, "Received Message from ${phoneNumber}");

        val csvDataList = messageBody?.split(",(?=([^']*'[^']*')*[^']*\$)".toRegex()) ?: listOf()

        val dataToSend: Map<String,String> = mapOf(
            IncommingMessagesRepoContants.COL_DATE to formatDate(timeStamp),
            IncommingMessagesRepoContants.COL_API_KEY to defaultToEmpty(phoneNumber),
            IncommingMessagesRepoContants.COL_SOME_IMPORTANT_FIELD to csvDataList.get(0),
            IncommingMessagesRepoContants.COL_SOME_OTHER_FIELD to csvDataList.get(1),
            IncommingMessagesRepoContants.COL_FIELD_IMPORTANT to csvDataList.get(2),
            IncommingMessagesRepoContants.COL_STILL_RENAMING to csvDataList.get(3),
            IncommingMessagesRepoContants.COL_STILL_PICKING_NAMES to csvDataList.get(4),
            IncommingMessagesRepoContants.COL_MIKE_NAME to csvDataList.get(5),
            IncommingMessagesRepoContants.COL_MIKE_NAME to csvDataList.get(6),
            IncommingMessagesRepoContants.COL_TOM_NAME to csvDataList.get(7),
            IncommingMessagesRepoContants.COL_OTHER_RENAME to csvDataList.get(8),
            IncommingMessagesRepoContants.COL_LALA_NAME to csvDataList.get(11)
        )

        val jsonMessage = JSONObject(dataToSend)

        return if (sendData(endPoint!!, jsonMessage))  Result.success(Data.Builder().putAll(dataToSend).build()) else Result.failure()
    }

    fun defaultToEmpty(anyValue:String?):String {
        return anyValue ?: ""
    }

    /**
     * Takes a date converts to String
     */
    fun formatDate(dateAsLong:Long):String{
        return SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
            .format(Date(dateAsLong))
    }

    /**
     * Sends data over POST on provided endpoint. returns true if sending was successfull
     * else otheriswise
     */
    fun sendData(endPoint:String, body:JSONObject):Boolean{
        val url = URL(endPoint)
        try {

            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setReadTimeout(10000)
            conn.setConnectTimeout(15000)
            conn.setRequestMethod("POST")
            conn.setDoInput(true)
            conn.setDoOutput(true)

            conn.setRequestProperty("Content-Type","application/json")

            val outputStream = BufferedOutputStream(conn.getOutputStream())
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
            writer.write(body.toString())
            writer.flush()
            writer.close()
            outputStream.close()
            val responseCode: Int = conn.responseCode;

            Log.d(TAG,"Made request response code was ${responseCode}")

            conn.disconnect()
            return (responseCode >= 200 && responseCode < 300)
        } catch (throwable:Throwable){
            Log.e(TAG, "Could not connect to server")

            return false
        }

    }
}