package hh.app.oldmanemu.beans

data class SingleTopicBean(
    var title:String,
    var user:User,
    var postTime:String,
    var categories:ArrayList<String>?=ArrayList<String>(),
    var content:String,
    var commentDetail:CommentListBean
)
