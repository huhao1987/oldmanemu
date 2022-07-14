package hh.app.oldmanemu.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hh.app.oldmanemu.beans.CommentBean
import hh.app.oldmanemu.beans.CommentListBean
import hh.app.oldmanemu.beans.SingleTopicBean
import hh.app.oldmanemu.beans.User
import hh.app.oldmanemu.retrofit.GetPespo
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleTopicViewModel : ViewModel() {
    private val singletopicData = MutableLiveData<SingleTopicBean>()
    private var commentListData: MutableLiveData<ArrayList<CommentBean>>? = null
    private var sendCommentData: MutableLiveData<Boolean>? = null
    var singletopicError=MutableLiveData<String>()
    fun getSingleTopic(url: String): MutableLiveData<SingleTopicBean> {
        viewModelScope.launch {
            GetPespo.getSinglePage(url, object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.apply {
                        var htmlStr = this.string()
                        var document = Jsoup.parse(htmlStr)
                        //分类列表
//                        var categriesData =
//                            document.getElementsByClass("breadcrumb d-none d-md-flex").getOrNull(0)
//                        var categrieslist = categriesData.getElementsByClass("breadcrumb-item")
                        //整体发布内容
                        var postContent = document.getElementsByClass("card-body").getOrNull(0)
                        if(postContent!=null) {
                            if (postContent.text().contains("主题不存在"))
                                singletopicError.postValue("主题不存在")
                            else {
                                var titleDetail =
                                    postContent.getElementsByClass("media").getOrNull(0)
                                //标题
                                var title = titleDetail?.getElementsByTag("h4")?.text()?:""
                                //头像
                                var avatar = titleDetail?.getElementsByTag("img")?.attr("src")
                                //等级
                                var level = titleDetail?.getElementsByTag("i")?.text()
                                //用户名
                                var username =
                                    titleDetail?.getElementsByClass("username")?.getOrNull(0)?.text()
                                //发布时间
                                var postTime =
                                    titleDetail?.getElementsByClass("date text-grey ml-2")?.getOrNull(0)?.text()?:""
                                var likeNum =
                                    document.getElementsByClass("haya-post-like-post-user-count")
                                        .getOrNull(0)?.text()?.toInt()?:0
                                //内容
                                var content =
                                    postContent.getElementsByClass("message break-all").getOrNull(0)?.html()?:""
                                var user = User(userName = username?:"", avatar = avatar?:"", level = level?:"")
                                //评论
                                var commentDetail =
                                    document.getElementsByClass("card card-postlist").getOrNull(0)
                                var commentTitle =
                                    commentDetail?.getElementsByClass("card-title")?.getOrNull(0)
                                var commentNum =
                                    commentTitle?.getElementsByClass("posts")?.getOrNull(0)?.text()?:""
                                var commentListDetail =
                                    commentDetail?.getElementsByClass("list-unstyled postlist")
                                var commentPage = 0
                                var commentPageData =
                                    document.getElementsByClass("pagination my-4 justify-content-center flex-wrap")
                                if (commentPageData.isNotEmpty()) {
                                    var commentPages =
                                        commentPageData.getOrNull(0)?.getElementsByClass("page-item")
                                    commentPage = commentPages?.get(commentPages.size - 2)?.text()?.replace("...", "")?.toInt()?:0
                                }

                                var commentList = ArrayList<CommentBean>()
                                commentListDetail?.let {
                                    if (commentListDetail.isNotEmpty()) {
                                        var origincommentList =
                                            commentListDetail.getOrNull(0)?.getElementsByClass("media post")
                                        origincommentList?.forEach {
                                            var commentBean = CommentBean()
                                            commentBean.quoteid = it.attr("data-pid")
                                            commentBean.commentContent =
                                                it.getElementsByClass("message mt-1 break-all").getOrNull(0)?.html()?:""
                                            var avatar = it.getElementsByTag("img").attr("src")
                                            var level =
                                                it.getElementsByClass("padding-bottom:0;margin-bottom:0;color:; ")
                                                    .text()
                                            var username = it.getElementsByClass("username").text()
                                            var link = it.getElementsByTag("a").attr("href")
                                            commentBean.user = User(
                                                avatar = avatar,
                                                level = level,
                                                userName = username,
                                                userLink = link
                                            )
                                            commentBean.likeNum = likeNum
                                            commentBean.postTime =
                                                it.getElementsByClass("date text-grey ml-2").getOrNull(0)?.text()?:""
                                            commentList.add(commentBean)
                                        }
                                    }

                                }
                                var commentListBean =
                                    CommentListBean(
                                        commentNum = commentNum,
                                        commentList = commentList
                                    )
                                singletopicData.postValue(
                                    SingleTopicBean(
                                        title = title,
                                        content = content,
                                        user = user,
                                        postTime = postTime,
                                        commentDetail = commentListBean,
                                        commentPage = commentPage,
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }

            })
        }
        return singletopicData
    }

    fun getMoreComments(url: String,index:Int): MutableLiveData<ArrayList<CommentBean>> {
        var finUrl=url.replace(".htm","")
        commentListData = MutableLiveData<ArrayList<CommentBean>>()
            viewModelScope.launch {
                GetPespo.getMoreCommentlist(finUrl,index, object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        response.body()?.apply {
                            var htmlStr = this.string()
                            var document = Jsoup.parse(htmlStr)
                            var commentDetail =
                                document.getElementsByClass("card card-postlist").getOrNull(0)
                            var commentTitle = commentDetail?.getElementsByClass("card-title")?.getOrNull(0)
                            var commentNum = commentTitle?.getElementsByClass("posts")?.getOrNull(0)?.text()
                            var commentListDetail =
                                commentDetail?.getElementsByClass("list-unstyled postlist")
                            var commentPage=0
                            var commentPageData= document.getElementsByClass("pagination my-4 justify-content-center flex-wrap")
                            if(commentPageData.isNotEmpty()) {
                                var commentPages = commentPageData.getOrNull(0)?.getElementsByClass("page-item")
                                commentPage=commentPages?.get(commentPages.size-2)?.text()?.replace("...","")?.toInt()?:0
                            }
                            var commentList = ArrayList<CommentBean>()

                            if (commentListDetail!=null&&commentListDetail.isNotEmpty()) {
                                var origincommentList =
                                    commentListDetail.getOrNull(0)?.getElementsByClass("media post")
                                origincommentList?.forEach {
                                    var commentBean = CommentBean()
                                    commentBean.quoteid=it.attr("data-pid")
                                    commentBean.commentContent =
                                        it.getElementsByClass("message mt-1 break-all").getOrNull(0)?.html()?:""
                                    var avatar = it.getElementsByTag("img").attr("src")
                                    var level =
                                        it.getElementsByClass("padding-bottom:0;margin-bottom:0;color:; ")
                                            .text()
                                    var username = it.getElementsByClass("username").text()
                                    var link = it.getElementsByTag("a").attr("href")
                                    commentBean.postTime =
                                        it.getElementsByClass("date text-grey ml-2").getOrNull(0)?.text()?:""
                                    var likeNum=it.getElementsByClass("haya-post-like-post-user-count").getOrNull(0)?.text()?.toInt()?:0
                                    commentBean.likeNum=likeNum
                                    commentBean.user = User(
                                        avatar = avatar,
                                        level = level,
                                        userName = username,
                                        userLink = link
                                    )
                                    commentList.add(commentBean)
                                }
                                commentListData?.postValue(commentList)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })
            }
            return commentListData!!
        }

        fun postComment(topicid: String, message: String, quotepid: String = ""):MutableLiveData<Boolean>{
            sendCommentData=MutableLiveData<Boolean>()
            var finalMsg=message
//            +"<p>&nbsp;</p><p> -<span style=\"color: rgb(0, 112, 192);\">-来自简陋的手机客户端</span></p>"
            viewModelScope.launch {
                GetPespo.postComment(topicid,finalMsg,quotepid,object:Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        sendCommentData?.postValue(true)
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })
            }
            return sendCommentData!!
        }
}