package symbbuzz.com.symbbuzzlib.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.Settings
import com.google.firebase.iid.FirebaseInstanceId
import symbbuzz.com.symbbuzzlib.constants.Constants
import symbbuzz.com.symbbuzzlib.data.FirebaseInfo

class DBHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun registerEvent(eventName: String): Boolean {
        val db = writableDatabase
        val d_id = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)

        val values = ContentValues()
        values.put(Constants.FIREBASE_TOKEN, FirebaseInstanceId.getInstance().token)
        values.put(Constants.DEVICE_ID, d_id)
        values.put(Constants.EVENT_NAME, eventName)
        values.put(Constants.TIMESTAMP, System.currentTimeMillis())

        val newRowId = db.insert(Constants.TABLE_NAME, null, values)
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteEvent(eventName: String): Boolean {
        val db = writableDatabase
        val selection = Constants.EVENT_NAME + " LIKE ?"
        val selectionArgs = arrayOf(eventName)
        val result = db.delete(Constants.TABLE_NAME, selection, selectionArgs)
        return result != -1
    }

    fun getAllEvents(): ArrayList<FirebaseInfo> {
        val firebaseInfo = ArrayList<FirebaseInfo>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList<FirebaseInfo>()
        }

        var token: String
        var deviceId: String
        var event: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                token = cursor.getString(cursor.getColumnIndex(Constants.FIREBASE_TOKEN))
                deviceId = cursor.getString(cursor.getColumnIndex(Constants.DEVICE_ID))
                event = cursor.getString(cursor.getColumnIndex(Constants.EVENT_NAME))

                firebaseInfo.add(FirebaseInfo(token, deviceId, event))
                cursor.moveToNext()
            }
        }
        return firebaseInfo
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "symbbuzz.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                Constants.FIREBASE_TOKEN + " TEXT," +
                Constants.DEVICE_ID + " TEXT," +
                Constants.EVENT_NAME + " TEXT," +
                Constants.TIMESTAMP + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME
    }

}
