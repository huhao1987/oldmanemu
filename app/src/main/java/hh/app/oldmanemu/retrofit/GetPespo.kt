package hh.app.oldmanemu.retrofit

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Field
import java.util.*

class GetPespo {
    companion object {
        var baseUrl = "https://bbs.oldmanemu.net/"
        var cookie = ""
        var retrofit: Retrofit? = null

        fun init(cookie: String? = null): Retrofit {
            if (retrofit == null) {
                val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                var client = OkHttpClient.Builder()
                    .addInterceptor(CookieInterceptor(cookie))
                    .addInterceptor(interceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .build()
            }
            return retrofit!!
        }

        fun getHomePage(position:Int,callback: Callback<ResponseBody>) {
            var url=when(position){
                1->""
                2->"forum-1.htm"
                3->"forum-2.htm"
                4->"forum-3.htm"
                else->""
            }
            init()
                .create(OldmanService::class.java)
                .getHomepage(url)
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

        fun getMorePage(url:String,index: Int, callback: Callback<ResponseBody>) {
            init()
                .create(OldmanService::class.java)
                .getMorepage(url,index)
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

        fun getSinglePage(url: String, callback: Callback<ResponseBody>) {
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

        fun getMoreCommentlist(url: String, index: Int, callback: Callback<ResponseBody>) {
            init()
                .create(OldmanService::class.java)
                .getMoreCommentlist(url, index)
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

        fun postComment(
            topicid: String,
            message: String,
            //引用评论的id
            quotepid: String = "",
            callback: Callback<ResponseBody>
        ) {
            init()
                .create(OldmanService::class.java)
                .postComment(topicid,message,quotepid)
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

        fun postRead(
            nid:String,
            act:String="readone",
            callback: Callback<ResponseBody>
        ) {
            init()
                .create(OldmanService::class.java)
                .postRead(nid,act)
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
        fun getNotification(callback: Callback<ResponseBody>){
            init()
                .create(OldmanService::class.java)
                .getNotification()
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