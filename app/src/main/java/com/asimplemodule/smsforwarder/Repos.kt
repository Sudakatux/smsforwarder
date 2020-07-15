package com.asimplemodule.smsforwarder

import android.content.ContentValues
import android.content.Context
import android.database.Cursor


object IncommingMessagesRepoContants {
    const val TABLE_NAME = "INCOMMING_MESSAGES"
    const val COL_ID = "Id"

    const val COL_DATE = "date"
    const val COL_API_KEY = "api_key"
    const val COL_SOME_IMPORTANT_FIELD = "asdsd"
    const val COL_SOME_OTHER_FIELD = "ouioui"
    const val COL_FIELD_IMPORTANT = "dfgdxcv"
    const val COL_PICKING_RANDOM_NAMES = "dsdf"
    const val COL_STILL_PICKING_NAMES = "vcsdf"
    const val COL_MIKE_NAME = "bcbcvb"
    const val COL_TESTING_NAME = "tp435rsesure"
    const val COL_TOM_NAME = "jhkhteyrte"
    const val COL_OTHER_RENAME = "klfkhjldfghj"
    const val COL_STILL_RENAMING = "vxnmbcvsdht"
    const val COL_LALA_NAME = "rvkdsn"

    val COLS = listOf<String>(
        COL_DATE, COL_API_KEY, COL_SOME_IMPORTANT_FIELD, COL_SOME_OTHER_FIELD,
        COL_FIELD_IMPORTANT, COL_PICKING_RANDOM_NAMES, COL_STILL_PICKING_NAMES, COL_MIKE_NAME, COL_TESTING_NAME,
        COL_TOM_NAME, COL_OTHER_RENAME, COL_STILL_RENAMING, COL_LALA_NAME
    )
}

class IncommingMessagesRepo(private val context: Context) {
    fun getLast(limit: Int): List<Map<String, String>> {
        val dataHelper = DbHelper.getInstance(context)
        dataHelper.readableDatabase.use { db ->
            db.query(
                IncommingMessagesRepoContants.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                IncommingMessagesRepoContants.COL_ID+" DESC",
                limit.toString()
            ).use { cursor ->
                cursor.moveToFirst()
                return generateSequence { if(cursor.moveToNext()) cursor else null } // to Sequence
                        .map{ cursorKeyToPair(it) } // to keyToPair
                        .map{keyToPair -> mapOf(
                            *(IncommingMessagesRepoContants.COLS.map { keyToPair(it) }).toTypedArray() // to map of Pairs
                        )}.toList()
            }
        }
    }

    fun countMessages():Int{
        val dataHelper = DbHelper.getInstance(context)
        dataHelper.readableDatabase.use { db->
          db.rawQuery("SELECT COUNT(*) FROM ${IncommingMessagesRepoContants.TABLE_NAME}",
              emptyArray()).use { cursor ->
              if(cursor.count > 0) {
                  cursor.moveToFirst()
                  return cursor.getInt(0)
              }
              return 0
          }
        }
    }

    fun insert(item: Map<String, String>): Long {
        val dataHelper = DbHelper.getInstance(context)

        dataHelper.writableDatabase.use { db ->
            val contentValues = ContentValues(IncommingMessagesRepoContants.COLS.size)
            IncommingMessagesRepoContants.COLS.forEach({contentValues.put(it,item.get(it))})
            return db.insert(IncommingMessagesRepoContants.TABLE_NAME, null, contentValues)
        }
    }

    fun cursorKeyToPair(cursor: Cursor): (String) -> Pair<String, String> {
        return fun(key: String): Pair<String, String> {
            return key to cursor.getString(
                cursor.getColumnIndex(
                    key
                )
            )
        }
    }
}

