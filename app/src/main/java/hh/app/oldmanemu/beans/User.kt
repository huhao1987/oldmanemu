package hh.app.oldmanemu.beans

/**
 * 用户信息
 * @author Hao
 */
data class User(
    var userName:String="",
    var userLink: String="my.htm",
    var avatar: String="",
    var level:String="",
    var postNum:Int=0,
    var replyNum:Int=0,
    var featuredPostNum:Int=0,
    var userGroup:String="",
    var postTime:String="",
    var postToplist: ArrayList<TopicBean>? = ArrayList<TopicBean>()
)
