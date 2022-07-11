package hh.app.oldmanemu.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * network connection
 * @author Hao
 */
interface OldmanService {
    @GET("/")
    fun getHomepage(): Call<ResponseBody>

    @GET("/index-{index}.htm")
    fun getMorepage(@Path("index") index: Int): Call<ResponseBody>

    @GET("{url}")
    fun getSinglepage(@Path("url") url: String): Call<ResponseBody>

    @GET("{url}-{index}.htm")
    fun getMoreCommentlist(@Path("url") url: String, @Path("index") index: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/post-create-{topicid}-0.htm")
    fun postComment(
        @Path("topicid") topicid: String,
        @Field("message") message: String,
        @Field("quotepid") quotepid: String="",
        @Field("doctype") doctype: Int = 0
    ): Call<ResponseBody>

}