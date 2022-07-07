package hh.app.oldmanemu.beans

data class CommentListBean(
    var commentNum: String = "",
    var commentList: ArrayList<CommentBean>? = ArrayList<CommentBean>()
)

