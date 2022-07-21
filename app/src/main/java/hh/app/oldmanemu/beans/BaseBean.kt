package hh.app.oldmanemu.beans

import com.google.gson.annotations.SerializedName


data class BaseBean(
    @SerializedName("code")
    var code: String="-1",
    @SerializedName("message")
    var message: String=""
)