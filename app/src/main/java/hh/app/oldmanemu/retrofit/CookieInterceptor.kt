package hh.app.oldmanemu.retrofit

import android.util.Log
import hh.app.oldmanemu.OldmanApplication
import hh.app.oldmanemu.Utils.SharePerferencesUtil
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor(var cookie:String?=null) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var builder = chain.request().newBuilder()
        var tempcookie =
            cookie?:SharePerferencesUtil.sharedPreferences?.getString("cookie", null)
        Log.d("thecookie::",tempcookie?:"Null")
        tempcookie?.apply {
            builder.addHeader("Cookie", this)
        }
        return chain.proceed(builder.build())
//        if(!originalResponse.headers("Set-Cookie").isEmpty()){
//
//        }
    }
}