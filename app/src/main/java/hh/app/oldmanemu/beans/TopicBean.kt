package hh.app.oldmanemu.beans

/**
 *
 */
data class TopicBean(
    var link:String="",
    var topic:String="",
    var postAvatar:String="",
    var postDetail:String="",
    var replyDetail:String="",
    var replyTime:String="",
    var replyNum:String="",
    var favNum:String="",
    var user: User?=User()
)
