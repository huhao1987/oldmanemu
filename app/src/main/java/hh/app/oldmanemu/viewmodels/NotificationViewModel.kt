package hh.app.oldmanemu.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hh.app.oldmanemu.beans.NotificationBean
import hh.app.oldmanemu.beans.NotificatonPageBean
import hh.app.oldmanemu.beans.User
import hh.app.oldmanemu.retrofit.GetPespo
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewModel: ViewModel() {
    private val NotificationData=MutableLiveData<NotificatonPageBean>()
    private val notifyReadData=MutableLiveData<Boolean>()
    fun getNotificationData():MutableLiveData<NotificatonPageBean>{
        viewModelScope.launch {
            var notificatonPageBean=NotificatonPageBean()
            GetPespo.getNotification(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.apply {
                        var htmlStr = this.string()
                        var document = Jsoup.parse(htmlStr)
                        document.getElementById("my_main")?.let {
                            var notificationList=ArrayList<NotificationBean>()
                            it.getElementsByClass("notice media text-small ").forEach {
                                var notificationBean=NotificationBean()
                                notificationBean.dataid=it.attr("data-nid")
                                notificationBean.NotifyContent=it.getElementsByClass("message break-all mt-1").getOrNull(0)?.html()?:""
                                notificationList.add(notificationBean)
                                var user=User()
                                var userData=it.getElementsByClass("ml-1 mt-1 mr-3").getOrNull(0)
                                user.userLink=userData?.getElementsByTag("a")?.attr("href")?:""
                                user.avatar=userData?.getElementsByTag("img")?.attr("src")?:""
                                user.userName=it.getElementsByClass("username text-grey font-weight-bold mr-1").getOrNull(0)?.text()?:""
                                user.postTime=it.getElementsByClass("date text-grey").getOrNull(0)?.text()?:""
                                notificationBean.user=user
                            }
                            notificatonPageBean.notificationList=notificationList
                        }
                        NotificationData.postValue(notificatonPageBean)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

            })
        }
        return NotificationData
    }

    fun PostNotifyRead(nid:String):MutableLiveData<Boolean>{
        viewModelScope.launch {
            GetPespo.postRead(nid, callback = object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    notifyReadData.postValue(true)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    notifyReadData.postValue(false)
                }
            })
        }
        return notifyReadData
    }
}