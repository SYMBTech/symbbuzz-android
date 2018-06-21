package com.symb.foxpandasdk.data.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.Settings
import com.google.firebase.iid.FirebaseInstanceId
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.models.FirebaseInfo

internal class DBHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
        db.execSQL(SQL_CREATE_TOKEN_TABLE)
        db.execSQL(SQL_CREATE_CLASS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(SQL_DELETE_TABLE)
        db.execSQL(SQL_DELETE_TOKEN_TABLE)
        db.execSQL(SQL_DELETE_CLASS_TABLE)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun registerToken(token: String): Boolean {
        val db = writableDatabase

        val value = ContentValues()
        value.put(Constants.FIREBASE_TOKEN, token)

        val result = db.insert(Constants.TOKEN_TABLE, null, value)
        db.close()
        return result.toInt() != -1
    }

    fun getToken(): String {
        val db = writableDatabase
        var cursor: Cursor? = null
        var token = ""
        try {
            cursor = db.rawQuery("select * from " + Constants.TOKEN_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TOKEN_TABLE)
            return ""
        }

        if(cursor!!.moveToFirst()) {
            token = cursor.getString(cursor.getColumnIndex(Constants.FIREBASE_TOKEN))
        }
        db.close()
        return token
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteTokens(token: String): Boolean {
        val db = writableDatabase
        val selection = Constants.FIREBASE_TOKEN + " LIKE ?"
        val selectionArgs = arrayOf(token)
        val result = db.delete(Constants.TOKEN_TABLE, selection, selectionArgs)
        db.close()
        return result != -1
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
        db.close()
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteEvent(eventName: String): Boolean {
        val db = writableDatabase
        val selection = Constants.EVENT_NAME + " LIKE ?"
        val selectionArgs = arrayOf(eventName)
        val result = db.delete(Constants.TABLE_NAME, selection, selectionArgs)
        db.close()
        return result != -1
    }

    fun getAllEvents(): ArrayList<FirebaseInfo> {
        val firebaseInfo = ArrayList<FirebaseInfo>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_TABLE)
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
        db.close()
        return firebaseInfo
    }

    @Throws(SQLiteConstraintException::class)
    fun saveClassNameIntoDB(className: String): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(Constants.CLASS_NAME, className)

        val newRowId = db.insertWithOnConflict(Constants.CLASS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return newRowId.toInt() != -1
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteClass(className: String): Boolean {
        val db = writableDatabase
        val selection = Constants.CLASS_NAME + " LIKE ?"
        val selectionArgs = arrayOf(className)
        val result = db.delete(Constants.CLASS_TABLE, selection, selectionArgs)
        db.close()
        return result != -1
    }

    fun getAllClasses(): ArrayList<String> {
        val classes = ArrayList<String>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + Constants.CLASS_TABLE, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_CLASS_TABLE)
            return ArrayList<String>()
        }

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                classes.add(cursor.getString(cursor.getColumnIndex(Constants.CLASS_NAME)))
                cursor.moveToNext()
            }
        }
        db.close()
        return classes
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "foxpanda.db"

        private val SQL_CREATE_TABLE =
            "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                Constants.FIREBASE_TOKEN + " TEXT," +
                Constants.DEVICE_ID + " TEXT," +
                Constants.EVENT_NAME + " TEXT," +
                Constants.TIMESTAMP + " TEXT)"

        private val SQL_CREATE_TOKEN_TABLE =
            "CREATE TABLE " + Constants.TOKEN_TABLE + " (" +
                Constants.FIREBASE_TOKEN + " TEXT)"

        private val SQL_CREATE_CLASS_TABLE =
            "CREATE TABLE " + Constants.CLASS_TABLE + " (" +
                Constants.CLASS_NAME + " TEXT)"

        private val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME

        private val SQL_DELETE_TOKEN_TABLE = "DROP TABLE IF EXISTS " + Constants.TOKEN_TABLE

        private val SQL_DELETE_CLASS_TABLE = "DROP TABLE IF EXISTS " + Constants.CLASS_TABLE
    }

}
