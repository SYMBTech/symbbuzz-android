package com.symb.foxpandasdk.main

import android.content.Context
import android.util.Log
import com.symb.foxpandasdk.constants.Constants
import com.symb.foxpandasdk.data.dbHelper.DBHelper
import com.symb.foxpandasdk.utils.CommonUtils
import com.symb.foxpandasdk.utils.NetworkUtil

class FoxPanda {

    enum class LogLevel {
        BASIC,
        BODY,
        HEADER,
        NONE
    }

    companion object {

        private const val TAG = "DBElements"

        fun register(serverKey: String, pandaId: String) {

        }

        fun setLog(logEnable: Boolean, loglevel: LogLevel?) {
            if(loglevel != null) {
                val level = getLevelString(loglevel)
                NetworkUtil.initRetrofit(logEnable, level)
            } else {
                NetworkUtil.initRetrofit(false, Constants.DEFAULT_LOG_LEVEL)
            }
        }

        fun initialize(context: Context) {
            val db = DBHelper(context)
            val classes = CommonUtils.getClassesOfPackage(context)
            val dbClasses = db.getAllInternalClasses()
            if(dbClasses.size > 0) {
                if(dbClasses.size != classes.size) {
                    //update on server
                    FPLogger(TAG, "sizes don't match")
                    updateClassesToServer(context, classes)
                } else {
                    for (i in classes.indices) {
                        if (!classes[i].equals(dbClasses[i])) {
                            //update on server
                            FPLogger(TAG, "classes don't match")
                            updateClassesToServer(context, classes)
                        }
                    }
                }
            } else {
                FPLogger(TAG, "first time insertion")
                updateClassesToServer(context, classes)
            }
        }

        //update classes to the server
        private fun updateClassesToServer(context: Context, classes: Array<String>) {
            val db = DBHelper(context)
            db.deleteAllClass()
            db.deleteAllInternalClass()
            classes.forEach {
                db.saveInternalClassNameIntoDB(it)
                if (!it.contains("$") && !it.contains("foxpandasdk"))
                    db.saveClassNameIntoDB(it)
            }
            val cls = db.getAllClasses()
            cls.forEach {
                FPLogger("clsName", it)
            }
            val icls = db.getAllInternalClasses()
            icls.forEach {
                FPLogger("iclsName", it)
            }
        }

        private fun getLevelString(loglevel: LogLevel): String {
            if(loglevel == LogLevel.BASIC)
                return Constants.BASIC
            else if(loglevel == LogLevel.NONE)
                return Constants.NONE
            else if(loglevel == LogLevel.HEADER)
                return Constants.HEADER
            else
                return Constants.DEFAULT_LOG_LEVEL
        }

        internal fun FPLogger(tag: String, message: String) {
            Log.e(tag, message)
        }

    }

}
