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

        fun getClassesName(context: Context) {
            val db = DBHelper(context)
            val classes = CommonUtils.getClassesOfPackage(context)
            classes.forEach {
                db.saveClassNameIntoDB(it)
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
