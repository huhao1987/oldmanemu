package hh.app.oldmanemu.beans

data class CommentBean(
    var commentContent:String="",
    var quoteid:String="",
    var user: User?=User()
)
