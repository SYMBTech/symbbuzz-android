package com.symb.foxpandasdk.main

import android.content.Context
import android.widget.Toast

class FoxPanda: BaseActivity() {
    companion object {
        fun toast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun register(serverKey: String, pandaId: String) {

        }
    }
}
