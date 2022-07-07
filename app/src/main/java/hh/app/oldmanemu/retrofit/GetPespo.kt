package hh.app.oldmanemu.retrofit

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class GetPespo {
    companion object {
        var baseUrl = "https://bbs.oldmanemu.net/"
        var cookie = ""
        var retrofit: Retrofit? = null

        fun init():Retrofit {
            if(retrofit==null) {
                val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                var client = OkHttpClient.Builder()
                    .addInterceptor(CookieInterceptor())
                    .addInterceptor(interceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .build()
            }
            return retrofit!!
        }

        fun getHomePage(callback: Callback<ResponseBody>) {
            init()
                .create(OldmanService::class.java)
                .getHomepage()
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        callback.onResponse(call, response)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        callback.onFailure(call, t)
                    }
                })
        }

        fun getSinglePage(url:String,callback: Callback<ResponseBody>){
            init()
                .create(OldmanService::class.java)
                .getSinglepage(url)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        callback.onResponse(call, response)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        callback.onFailure(call, t)
                    }
                })
        }

    }
}