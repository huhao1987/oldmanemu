package hh.app.oldmanemu.beans

data class CommentBean(
    var commentContent:String="",
    var postTime:String="",
    var likeNum:Int=0,
    var quoteid:String="",
    var user: User?=User()
)
