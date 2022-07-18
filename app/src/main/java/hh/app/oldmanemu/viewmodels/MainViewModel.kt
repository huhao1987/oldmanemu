package hh.app.oldmanemu.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hh.app.oldmanemu.beans.HomePageBean
import hh.app.oldmanemu.beans.NaviBar
import hh.app.oldmanemu.beans.TopicBean
import hh.app.oldmanemu.beans.User
import hh.app.oldmanemu.retrofit.GetPespo
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 首页
 *@author
 */
class MainViewModel : ViewModel() {
    private val homePageData = MutableLiveData<HomePageBean>()
    private var topicListData: MutableLiveData<ArrayList<TopicBean>>? = null


    fun getHomePageData(position:Int=0): MutableLiveData<HomePageBean> {
        viewModelScope.launch {
            GetPespo.getHomePage(position,object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.apply {
                        var htmlStr = this.string()
                        var document = Jsoup.parse(htmlStr)
                        document.getElementsByClass("navbar navbar-expand-lg navbar-dark bg-dark")
                            .apply {
                                if (isNotEmpty()) {
                                    var baseNaviBar = getOrNull(0)
                                    var naviBar =
                                        baseNaviBar?.getElementsByClass("navbar-nav mr-auto")
                                            ?.getOrNull(0)
                                            ?.getElementsByClass(
                                                "nav-link"
                                            ) ?: null
                                    var homePageBean = HomePageBean()
                                    //Nav bar
                                    var navibarList = ArrayList<NaviBar>()
                                    naviBar?.forEach {
                                        var link = it.getElementsByTag("a")
                                        navibarList.add(NaviBar(link.attr("href"), link.text()))
                                    }
                                    homePageBean.naviBarList = navibarList
                                    //User info

                                    var user = baseNaviBar?.getElementsByClass("nav-item username")
                                    if (user != null && user.isNotEmpty())
                                        homePageBean.userInfo = User(
                                            userName = user.text(),
                                            avatar = user.getOrNull(0)?.getElementsByTag("img")
                                                ?.attr("src") ?: ""
                                        )
                                    //alert
                                    homePageBean.alert =
                                        document.getElementsByClass("alert alert-warning alert-dismissible fade show")
                                            .getOrNull(0)?.text() ?: ""
                                    //topic list
                                    var topiclistHtml =
                                        document.getElementsByClass("list-unstyled threadlist mb-0")
                                            .getOrNull(0)
                                            ?.getElementsByTag("li") ?: ArrayList<Element>()
                                    var topicList = ArrayList<TopicBean>()
                                    for (element in topiclistHtml) {
                                        var topicInfo =
                                            element.getElementsByClass("subject break-all")
                                                .getOrNull(0)
                                        var posterInfo = element.getElementsByTag("a").getOrNull(0)
                                        var extraInfo =
                                            element.getElementsByClass("d-flex justify-content-between small mt-1")
                                                .getOrNull(0)
                                        var numInfo =
                                            extraInfo?.getElementsByClass("text-muted small")
                                                ?.getOrNull(0)
                                        var topic = topicInfo?.text()?:""
                                        var link = topicInfo?.getElementsByTag("a")?.attr("href")?:""
                                        var postAvatar =
                                            posterInfo?.getElementsByTag("img")?.getOrNull(0)
                                                ?.attr("src")?:""
                                        var postDetail =
                                            if (extraInfo?.getElementsByClass("date text-grey hidden-sm") != null
                                                && extraInfo.getElementsByClass(
                                                    "date text-grey hidden-sm")
                                                    .isNotEmpty()
                                            )
                                                extraInfo.getElementsByClass("date text-grey hidden-sm")
                                                    .getOrNull(0)?.text() else ""

                                        var replyDetail =
                                            if (extraInfo?.getElementsByClass("username text-grey mr-1")!=null&&extraInfo.getElementsByClass("username text-grey mr-1")
                                                    .isNotEmpty()
                                            )
                                                extraInfo.getElementsByClass("username text-grey mr-1")
                                                    .getOrNull(0)?.text() else ""
                                        var replyTime = if (
                                            extraInfo
                                                ?.getElementsByClass("text-grey")!=null&&extraInfo
                                                .getElementsByClass("text-grey").size > 2
                                        ) extraInfo
                                            .getElementsByClass("text-grey").get(2).text()
                                        else extraInfo
                                            ?.getElementsByClass("text-grey")?.get(
                                                extraInfo
                                                    .getElementsByClass("text-grey").size - 1
                                            )?.text()
                                        var topicBean = TopicBean(
                                            topic = topic,
                                            link = link,
                                            postAvatar = postAvatar,
                                            postDetail = postDetail?:"",
                                            replyDetail = replyDetail?:"",
                                            replyTime = replyTime?:"",
                                            replyNum = numInfo?.getElementsByClass("ml-2")?.get(1)
                                                ?.text()
                                                ?.replace("\"", "")
                                                ?.replace(" ", "")?:"",
                                            favNum = numInfo
                                                ?.getElementsByClass("ml-2")?.get(2)?.text()
                                                ?.replace("\"", "")
                                                ?.replace(" ", "")?:""
                                        )
                                        topicList.add(topicBean)
                                    }
                                    homePageBean.topicList = topicList

                                    //notification num
                                    var notify =
                                        document.getElementsByClass("d-none unread badge badge-danger badge-pill")
                                    if (notify.isNotEmpty())
                                        homePageBean.notifyNum = notify.text().toInt()
                                    //Page index
                                    var basePage = document.getElementsByClass("page-item")
                                    homePageBean.endPage =
                                        basePage[basePage.size - 2].text().replace("...", "")
                                            .toInt()
                                    homePageData.postValue(homePageBean)
                                }
                            }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })
        }
        return homePageData
    }

    fun getMoreData(url:String,index: Int): MutableLiveData<ArrayList<TopicBean>> {
        topicListData = MutableLiveData<ArrayList<TopicBean>>()
        viewModelScope.launch {
            GetPespo.getMorePage(url,index, object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.apply {
                        var htmlStr = this.string()
                        var document = Jsoup.parse(htmlStr)
                        var topiclistHtml =
                            document.getElementsByClass("list-unstyled threadlist mb-0")
                                .getOrNull(0)
                                ?.getElementsByTag("li")?:ArrayList<Element>()
                        var topicList = ArrayList<TopicBean>()
                        for (element in topiclistHtml) {
                            var topicInfo =
                                element.getElementsByClass("subject break-all").getOrNull(0)
                            var posterInfo = element.getElementsByTag("a").getOrNull(0)
                            var extraInfo =
                                element.getElementsByClass("d-flex justify-content-between small mt-1")
                                    .getOrNull(0)
                            var numInfo =
                                extraInfo?.getElementsByClass("text-muted small")?.getOrNull(0)
                            var topic = topicInfo?.text()
                            var link = topicInfo?.getElementsByTag("a")?.attr("href")
                            var postAvatar = posterInfo?.getElementsByTag("img")?.getOrNull(0)
                                ?.attr("src")
                            var postDetail =
                                if (extraInfo?.getElementsByClass("date text-grey hidden-sm")!=null&&extraInfo.getElementsByClass("date text-grey hidden-sm")
                                        .isNotEmpty()
                                )
                                    extraInfo.getElementsByClass("date text-grey hidden-sm")
                                        .getOrNull(0)?.text() else ""
                            var replyDetail =
                                if (extraInfo?.getElementsByClass("username text-grey mr-1")!=null&&extraInfo.getElementsByClass("username text-grey mr-1")
                                        .isNotEmpty()
                                )
                                    extraInfo.getElementsByClass("username text-grey mr-1")
                                        .getOrNull(0)?.text() else ""
                            var replyTime = if (extraInfo
                                    ?.getElementsByClass("text-grey")!=null&&extraInfo
                                    .getElementsByClass("text-grey").size > 2
                            ) extraInfo
                                .getElementsByClass("text-grey").get(2).text()
                            else extraInfo
                                ?.getElementsByClass("text-grey")?.get(
                                    extraInfo
                                        .getElementsByClass("text-grey").size - 1
                                )?.text()
                            var topicBean = TopicBean(
                                topic = topic?:"",
                                link = link?:"",
                                postAvatar = postAvatar?:"",
                                postDetail = postDetail?:"",
                                replyDetail = replyDetail?:"",
                                replyTime = replyTime?:"",
                                replyNum = numInfo?.getElementsByClass("ml-2")?.get(1)
                                    ?.text()
                                    ?.replace("\"", "")
                                    ?.replace(" ", "")?:"",
                                favNum = numInfo
                                    ?.getElementsByClass("ml-2")?.get(2)?.text()
                                    ?.replace("\"", "")
                                    ?.replace(" ", "")?:""
                            )
                            topicList.add(topicBean)
                        }
                        topicListData?.postValue(topicList)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })
        }
        return topicListData!!
    }
}