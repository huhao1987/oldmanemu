package hh.app.oldmanemu.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * network connection
 * @author Hao
 */
interface OldmanService {

    //Home page
    @GET("/")
    fun getHomepage(): Call<ResponseBody>

    //More pages
    @GET("/index-{index}.htm")
    fun getMorepage(@Path("index") index: Int): Call<ResponseBody>

    //Single page
    @GET("{url}")
    fun getSinglepage(@Path("url") url: String): Call<ResponseBody>

    //more comment
    @GET("{url}-{index}.htm")
    fun getMoreCommentlist(@Path("url") url: String, @Path("index") index: Int): Call<ResponseBody>

    //Post comment
    @FormUrlEncoded
    @POST("/post-create-{topicid}-0.htm")
    fun postComment(
        @Path("topicid") topicid: String,
        @Field("message") message: String,
        @Field("quotepid") quotepid: String="",
        @Field("doctype") doctype: Int = 0
    ): Call<ResponseBody>

    //get notification
    @GET("/my-notice.htm")
    fun getNotification():Call<ResponseBody>

    @FormUrlEncoded
    @POST("/my-notice.htm")
    fun postRead(
        @Field("nid") nid: String,
        @Field("act") act: String="readone"
        ): Call<ResponseBody>


}