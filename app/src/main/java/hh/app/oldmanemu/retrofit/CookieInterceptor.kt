package hh.app.oldmanemu.retrofit

import hh.app.oldmanemu.Utils.SharePerferencesUtil
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var builder=chain.request().newBuilder()
        SharePerferencesUtil.sharedPreferences?.getString("cookie",null)?.apply {
            builder.addHeader("Cookie",this)
        }
        return  chain.proceed(builder.build())
//        if(!originalResponse.headers("Set-Cookie").isEmpty()){
//
//        }
    }
}