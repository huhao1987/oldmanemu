package hh.app.oldmanemu.Utils

import android.content.Context
import android.content.SharedPreferences

class SharePerferencesUtil(var context: Context) {
    companion object {
        var sharedPreferences: SharedPreferences? = null

        fun shareIntance(context: Context): SharedPreferences {
            if (sharedPreferences == null)
                sharedPreferences = context.getSharedPreferences("oldman", Context.MODE_PRIVATE)
            return sharedPreferences!!
        }
    }
}