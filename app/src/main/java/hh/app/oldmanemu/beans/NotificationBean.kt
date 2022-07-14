package hh.app.oldmanemu.beans

data class NotificationBean(
    var user: User?=User(),
    var NotifyTime:String="",
    var NotifyContent:String="",
    var unread:Boolean=false,
    var dataid:String=""
)
