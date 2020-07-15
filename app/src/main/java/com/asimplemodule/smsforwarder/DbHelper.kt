package com.asimplemodule.smsforwarder

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DbHelper(context: Context) : SQLiteOpenHelper(context, "example.db", null, 4) {
    companion object{
        private const val TAG = "DBHelper"
        private var databaseHelper: DbHelper? = null

        @Synchronized
        fun getInstance(context: Context): DbHelper {
            if (databaseHelper == null) {
                databaseHelper = DbHelper(context)
            }
            return databaseHelper as DbHelper
        }
    }

    val incommingMessagesTableCreate = "CREATE TABLE IF NOT EXISTS ${IncommingMessagesRepoContants.TABLE_NAME} (" +
            "${IncommingMessagesRepoContants.COL_ID} integer PRIMARY KEY autoincrement," +
            "${IncommingMessagesRepoContants.COL_API_KEY} text,"+
            "${IncommingMessagesRepoContants.COL_DATE} text," +
            "${IncommingMessagesRepoContants.COL_SOME_IMPORTANT_FIELD} text," +
            "${IncommingMessagesRepoContants.COL_SOME_OTHER_FIELD} text," +
            "${IncommingMessagesRepoContants.COL_FIELD_IMPORTANT} text," +
            "${IncommingMessagesRepoContants.COL_PICKING_RANDOM_NAMES} text," +
            "${IncommingMessagesRepoContants.COL_STILL_PICKING_NAMES} text," +
            "${IncommingMessagesRepoContants.COL_MIKE_NAME} text," +
            "${IncommingMessagesRepoContants.COL_TESTING_NAME} text," +
            "${IncommingMessagesRepoContants.COL_TOM_NAME} text," +
            "${IncommingMessagesRepoContants.COL_OTHER_RENAME} text," +
            "${IncommingMessagesRepoContants.COL_STILL_RENAMING} text," +
            "${IncommingMessagesRepoContants.COL_LALA_NAME} text)"

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG,"Creating table ${IncommingMessagesRepoContants.TABLE_NAME}")
        db.execSQL(incommingMessagesTableCreate)


    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}