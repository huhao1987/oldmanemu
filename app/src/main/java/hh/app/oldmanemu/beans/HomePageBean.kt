package hh.app.oldmanemu.beans

/**
 * 主页数据
 * @author Hao
 */
data class HomePageBean(
    var naviBarList:ArrayList<NaviBar>?=ArrayList(),
    var userInfo:User?=null,
    var notifyNum:Int=0,
    var alert:String="",
    var topicList:ArrayList<TopicBean>?=ArrayList(),
    var endPage:Int=0
)
