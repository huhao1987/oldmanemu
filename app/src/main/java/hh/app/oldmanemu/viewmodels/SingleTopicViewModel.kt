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
                        var categriesData =
                            document.getElementsByClass("breadcrumb d-none d-md-flex").get(0)
                        var categrieslist = categriesData.getElementsByClass("breadcrumb-item")
                        //整体发布内容
                        var postContent = document.getElementsByClass("card card-thread").get(0)

                        var titleDetail = postContent.getElementsByClass("media").get(0)
                        //标题
                        var title = titleDetail.getElementsByTag("h4").text()
                        //头像
                        var avatar = titleDetail.getElementsByTag("img").attr("src")
                        //等级
                        var level = titleDetail.getElementsByTag("i").text()
                        //用户名
                        var username = titleDetail.getElementsByClass("username").get(0).text()
                        //发布时间
                        var postTime =
                            titleDetail.getElementsByClass("date text-grey ml-2").get(0).text()
                        //内容
                        var content = postContent.getElementsByClass("message break-all").get(0)
                        var user = User(userName = username, avatar = avatar, level = level)
                        //评论
                        var commentDetail = document.getElementsByClass("card card-postlist").get(0)
                        var commentTitle = commentDetail.getElementsByClass("card-title").get(0)
                        var commentNum = commentTitle.getElementsByClass("posts").get(0).text()
                        var commentListDetail =
                            commentDetail.getElementsByClass("list-unstyled postlist")
                        var commentList = ArrayList<CommentBean>()
                        if (commentListDetail.isNotEmpty()) {
                            var origincommentList =
                                commentListDetail.get(0).getElementsByClass("media post")
                            origincommentList.forEach {
                                var commentBean = CommentBean()
                                commentBean.commentContent =
                                    it.getElementsByClass("message mt-1 break-all").get(0).html()
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
                                commentList.add(commentBean)
                            }
                        }
                        var commentListBean =
                            CommentListBean(commentNum = commentNum, commentList = commentList)
                        singletopicData.postValue(
                            SingleTopicBean(
                                title = title,
                                content = content.html(),
                                user = user,
                                postTime = postTime,
                                commentDetail = commentListBean
                            )
                        )
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

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })
            }
            return commentListData!!
        }

}